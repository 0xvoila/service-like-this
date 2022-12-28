package org.example;


import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.*;

public class Database {

    final static int PAGE_SIZE = 10;
    static TreeMap<Integer, String> DB = new TreeMap<>();

    static TreeNode<String> merkleRoot = null;

    static int FLOOR_TOKEN = 0;
    static int CEILING_TOKEN = 3;

    static io.grpc.Server server;



    public static void main(String args[]) throws IOException, InterruptedException {

        start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        server.awaitTermination();

//        database.insert(1,"A");
//        database.insert(2,"B");
//        database.insert(3,"C");
//        database.insert(4,"D");
//        database.insert(5,"E");
//        database.insert(6,"F");
//
//        ArrayList<TreeNode<String>> leafNodeHashList = database.prepareMerkle();
//        database.merkleRoot = database.createMerkle(leafNodeHashList);
//
//        database.printMerkle();
//
//        database.auditMerkle(database.merkleRoot, database.merkleRoot);
    }

    public void insert(int key, String value){

        DB.put(key, value);
    }

    public static ArrayList<TreeNode<String>> prepareMerkle(){
        ArrayList<TreeNode<String>> newNodes = new ArrayList<>();
        for(int i = FLOOR_TOKEN; i < CEILING_TOKEN; i++){
            String value = DB.get(i);

            if ( value != null){
                newNodes.add(new ArrayMultiTreeNode<>(value));
            }
            else {
                newNodes.add(new ArrayMultiTreeNode<>("^"));
            }
        }


        return newNodes;
    }

//    Run it every 2 second to create a new Merkle

    public static TreeNode<String> createMerkle (ArrayList<TreeNode<String>> levelNNodesList){

        ArrayList<TreeNode<String>> newNodes = new ArrayList<>();

        if(levelNNodesList.size() == 0 ){
            return new ArrayMultiTreeNode<>("");
        }

        if(levelNNodesList.size() == 1){
            return levelNNodesList.get(0);
        }

//        if it is odd then replicate the last node with itself to make it even
        if(levelNNodesList.size()%2 != 0){
            levelNNodesList.add(new ArrayMultiTreeNode<>(levelNNodesList.get(levelNNodesList.size() - 1).data()));
        }

        for(int i=0; i < levelNNodesList.size(); i= i + 2){

            TreeNode<String> t = new ArrayMultiTreeNode<>(levelNNodesList.get(i).data() + levelNNodesList.get(i+1).data());
            newNodes.add(t);
            t.add(levelNNodesList.get(i));
            t.add(levelNNodesList.get(i + 1));

        }

        return createMerkle(newNodes);
    }

    public void printMerkle(){

        // Iterating over the tree elements using foreach
        for (TreeNode<String> node : merkleRoot) {
            System.out.println(node.data()); // any other action goes here
        }
    }

    public static void auditMerkleV2(TreeNode<String> m1, TreeNode<String> m2){

        ArrayList<TreeNode<String>> m1List = (ArrayList<TreeNode<String>>) m1.preOrdered();
        ArrayList<TreeNode<String>> m2List = (ArrayList<TreeNode<String>>) m2.preOrdered();

        int i=0;
        while( i < m1List.size()){
            if(m1List.get(i).equals(m2List.get(i)) && m1List.get(i).isRoot()){
                System.out.println("Whole tree is in sync");
                break;
            }
            else if(m1List.get(i).equals(m2List.get(i)) && !m1List.get(i).isRoot()){


            }
        }

    }
    public static void auditMerkle(TreeNode<String> m1, TreeNode<String> m2){

        if ( m1 == null && m2 == null){
            return;
        }

        if(m1.data().equals(m2.data())){
            return;
        }

        else {
            Iterator<TreeNode<String>> x = m1.iterator();
            Iterator<TreeNode<String>> y = m2.iterator();
            while(x.hasNext() && y.hasNext()){
                TreeNode<String> xx = x.next();
                TreeNode<String> yy = y.next();
                if (xx.isLeaf() && yy.isLeaf()){
                    System.out.println("data to sync is " + xx.data());
                    auditMerkle(null, null);
                }
                else{
                    auditMerkle(xx,yy);
                }

            }

        }
    }

    public static void start(int serverPort, int clientPort ) throws IOException {
        server = Grpc.newServerBuilderForPort(serverPort, InsecureServerCredentials.create()).addService(new DatabaseService())
                .build();

        server.start();

        TimerTask timerTask = new SyncData(clientPort); //reference created for TimerTask class
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 10000, 10000);
    }

    private static class DatabaseService extends org.example.DatabaseGrpcServiceGrpc.DatabaseGrpcServiceImplBase{

        @Override
        public void createRecord(org.example.Record request, StreamObserver<org.example.Record> responseObserver) {
            DB.put(request.getKey(), request.getValue());
            responseObserver.onNext(request);
            responseObserver.onCompleted();
        }

        @Override
        public void getMerkle(org.example.Empty request,
                              io.grpc.stub.StreamObserver<org.example.MerkleTree> responseObserver){
            org.example.MerkleTree.Builder merkleTree = org.example.MerkleTree.newBuilder();
            ArrayList<TreeNode<String>> leafNodeHashList = prepareMerkle();
            merkleRoot = createMerkle(leafNodeHashList);
            Iterator<TreeNode<String>> iterator = merkleRoot.iterator();
            while (iterator.hasNext()) {
                TreeNode<String> node = iterator.next();
                merkleTree.addNodeValue(node.data());
            }
            responseObserver.onNext(merkleTree.build());
            responseObserver.onCompleted();

        }
    }


    private static class SyncData extends TimerTask{

        int clientPort = 6001;

        public SyncData(int clientPort){
            this.clientPort = clientPort;
        }
        public void run(){
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", this.clientPort).usePlaintext();
            Channel channel = channelBuilder.build();
            org.example.DatabaseGrpcServiceGrpc.DatabaseGrpcServiceBlockingStub blockingStub = org.example.DatabaseGrpcServiceGrpc.newBlockingStub(channel);
            org.example.Empty empty = org.example.Empty.newBuilder().build();
            org.example.MerkleTree merkleTree = blockingStub.getMerkle(empty);
            if(merkleTree.getNodeValueList().size() > 0){
                TreeNode<String> replicaMerkle = deserialize(merkleTree.getNodeValueList(), 0);
                ArrayList<TreeNode<String>> leafNodeHashList = prepareMerkle();
                merkleRoot = createMerkle(leafNodeHashList);
                auditMerkle(merkleRoot, replicaMerkle);
            }
            else{
                System.out.println("No data to replicate");
            }

        }


        private TreeNode<String> deserialize(List<String> values, int index){

            String val = values.get(index);

            index = index + 1;
            if (val.equals("#")) return null;

            TreeNode<String> root = new ArrayMultiTreeNode<String>(val);
            root.add(deserialize(values, index));
            return root;
        }
    }
}


