package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.*;

public class Database{

    final static int PAGE_SIZE = 10;
    static HashMap<Long, HashMap<String, Object>> DB = new HashMap<>();

    static MerkleTreeV3.Node<HashMap<String, Object>> merkleRoot = null;

    static int FLOOR_TOKEN = 0;
    static int CEILING_TOKEN = 20;

    static io.grpc.Server server;



    public static void main(String args[]) throws IOException, InterruptedException {

        start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        server.awaitTermination();
    }


    public static ArrayList<MerkleTreeV3.Node<HashMap<String, Object>>> prepareMerkle(){
        ArrayList<MerkleTreeV3.Node<HashMap<String, Object>>> newNodes = new ArrayList<>();
        for(long i = FLOOR_TOKEN; i < CEILING_TOKEN; i++){
            HashMap<String, Object> record = DB.get(i);

            if( record != null){
                MerkleTreeV3.Node<HashMap<String, Object>> n = new MerkleTreeV3.Node<HashMap<String, Object>>(record.get("value").hashCode());
                record.put("key", Long.toString(i));
                n.setData(record);
                newNodes.add(n);
            }
            else {
                MerkleTreeV3.Node<HashMap<String, Object>> n = new MerkleTreeV3.Node<HashMap<String, Object>>("EMPTY".hashCode());
                HashMap<String, Object> data = new HashMap<>();
                data.put("key", Long.toString(i));
                data.put("value", "EMPTY");
                n.setData(data);
                newNodes.add(n);
            }

        }


        return newNodes;
    }

//    Run it every 2 second to create a new Merkle

    public static MerkleTreeV3.Node<HashMap<String, Object>> createMerkle (ArrayList<MerkleTreeV3.Node<HashMap<String, Object>>> levelNNodesList){

        return  new MerkleTreeV3<HashMap<String, Object>>().createFrom(levelNNodesList);

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
            ObjectMapper objectMapper = new ObjectMapper();
            try{
                TypeReference<HashMap<String, Object>> typeReference = new TypeReference<HashMap<String, Object>>() {};
                DB.put(request.getKey(), objectMapper.readValue(request.getValue(), typeReference));
            }
            catch(Exception e){
                System.out.println("Error in creating the record");
            }

            responseObserver.onNext(request);
            responseObserver.onCompleted();
        }

        @Override
        public void getMerkle(org.example.Empty request,
                              io.grpc.stub.StreamObserver<org.example.MerkleTree> responseObserver){
            org.example.MerkleTree.Builder merkleTree = org.example.MerkleTree.newBuilder();
            ArrayList<MerkleTreeV3.Node<HashMap<String, Object>>> leafNodeHashList = prepareMerkle();
            merkleRoot = createMerkle(leafNodeHashList);
            try{
                String s = new MerkleTreeV3<HashMap<String, Object>>().serialize(merkleRoot);
                merkleTree.setNode(s);
            }
            catch(Exception e){
                System.out.println(e.getMessage());
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

            MerkleTreeV3.Node<HashMap<String, Object>> replicaMerkle = new MerkleTreeV3<HashMap<String, Object>>().deSerialize(merkleTree.getNode());
            ArrayList<MerkleTreeV3.Node<HashMap<String, Object>>> leafNodeHashList = prepareMerkle();
            merkleRoot = createMerkle(leafNodeHashList);
            ArrayList<MerkleTreeV3.Node<HashMap<String, Object>>> diffList = new MerkleTreeV3<HashMap<String, Object>>().auditMerkle(merkleRoot, replicaMerkle, new ArrayList<MerkleTreeV3.Node<HashMap<String, Object>>>());

            try {
                System.out.println(new ObjectMapper().writeValueAsString(diffList));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


