package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;


public class MerkleTreeV3<T>{

    static ObjectMapper objectMapper = new ObjectMapper();

    static class Node<T>{

            T data;
            long hash;

            @JsonIgnore
            Node<T> leftNode;

            @JsonIgnore
            Node<T> rightNode;

            @JsonIgnore
            Node<T> parentNode;

            public Node(){

            }

            public Node(long  hash){
                this.hash = hash;
                this.leftNode = null;
                this.rightNode = null;
            }

            public T getData() {
                return data;
            }

            public void setData(T data) {
                this.data = data;
            }

            public long getHash() {
                return hash;
            }

            public void setHash(long hash) {
                this.hash = hash;
            }

            public Node<T> getLeftNode() {
                return leftNode;
            }

            public void setLeftNode(Node<T> leftNode) {
                this.leftNode = leftNode;
            }

            public Node<T> getRightNode() {
                return rightNode;
            }

            public void setRightNode(Node<T> rightNode) {
                this.rightNode = rightNode;
            }

            @JsonIgnore
            public Boolean isLeaf(){
                if (this.leftNode == null && this.rightNode == null){
                    return true;
                }
                else{
                    return false;
                }
            }

            @JsonIgnore
            public Boolean isRoot(){

                if (this.parentNode == null){
                    return true;
                }
                else{
                    return false;
                }
            }

            @JsonIgnore
            public Node<T> getParentNode() {
                return parentNode;
            }

            @JsonIgnore
            public void setParentNode(Node<T> parentNode) {
                this.parentNode = parentNode;
            }
        }


        public  Node<T> createFrom(ArrayList<Node<T>> sortedNodeList){

            ArrayList<MerkleTreeV3.Node<T>> newNodes = new ArrayList<>();

            if(sortedNodeList.size() == 0 ){
                return null;
            }
            if(sortedNodeList.size() == 1){
                return sortedNodeList.get(0);
            }

            if(sortedNodeList.size()%2 != 0){
                MerkleTreeV3.Node<T> lastNode = sortedNodeList.get(sortedNodeList.size() - 1);
                MerkleTreeV3.Node<T> replicatedNode = new MerkleTreeV3.Node<T>(lastNode.getHash());
                replicatedNode.setData(lastNode.getData());
                sortedNodeList.add(replicatedNode);
            }

            for(int i=0; i < sortedNodeList.size(); i= i + 2){

                MerkleTreeV3.Node<T> intermediateNode = new MerkleTreeV3.Node<T>();
                intermediateNode.setHash(sortedNodeList.get(i).getHash() + sortedNodeList.get(i+1).getHash());
                newNodes.add(intermediateNode);
                intermediateNode.setLeftNode(sortedNodeList.get(i));
                intermediateNode.setRightNode(sortedNodeList.get(i + 1));

                sortedNodeList.get(i).setParentNode(intermediateNode);
                sortedNodeList.get(i+1).setParentNode(intermediateNode);

            }

            return createFrom(newNodes);
        }

        public String serialize(Node<T> rootNode) throws JsonProcessingException {

            ArrayList<Node<T>> preOrderedList = new ArrayList<>();
            preOrderedList.add(rootNode);

            int i = 0;
            while (i != preOrderedList.size()) {

                if (preOrderedList.get(i).leftNode != null) {
                    preOrderedList.add(preOrderedList.get(i).leftNode);
                }
                if (preOrderedList.get(i).rightNode != null) {
                    preOrderedList.add(preOrderedList.get(i).rightNode);
                }

                i = i + 1;
            }

            String s = objectMapper.writeValueAsString(preOrderedList);
//            System.out.println(s);

            return s;
        }

        public  Node<T> deSerialize(String str){

            try{
                TypeReference<ArrayList<Node<T>>> typeRef = new TypeReference<ArrayList<Node<T>>>() {};
                ArrayList<Node<T>> preOrderedList = objectMapper.readValue(str, typeRef);

                int i = 0;
                while (i != preOrderedList.size()) {

                    if(2*i + 1 < preOrderedList.size()){
                        preOrderedList.get(i).leftNode = preOrderedList.get(2*i + 1);
                    }
                    if (2 * i + 2 < preOrderedList.size()){
                        preOrderedList.get(i).rightNode = preOrderedList.get(2*i + 2);
                    }

                    i = i + 1;
                }

                return preOrderedList.get(0);
            }
            catch(Exception exception){
                System.out.println(exception.getMessage());
            }
            return null;
        }

        public void auditMerkle(Node<T> m1, Node<T> m2){

            if ( m1 == null && m2 == null){
                return;
            }

            if(m1.getHash() == m2.getHash()){
                return;
            }

            if (m1.isLeaf() && m2.isLeaf()){
                System.out.println("Need to exchange these nodes " + m1.getData());
            }

            auditMerkle(m1.leftNode, m2.leftNode);
            auditMerkle(m1.rightNode, m2.rightNode);
        }

        public static void main(String args[]) throws JsonProcessingException {

            Node<String> n1 = new Node<>(1);
            Node<String> n2 = new Node<>(2);
            Node<String> n3 = new Node<>(3);
            Node<String> n4 = new Node<>(4);

            ArrayList<Node<String>> nodeList = new ArrayList<Node<String>>();
            nodeList.add(n1);
            nodeList.add(n2);
            nodeList.add(n3);
            nodeList.add(n4);

            Node<String> rootNode = new MerkleTreeV3<String>().createFrom(nodeList);

            String s = new MerkleTreeV3<String>().serialize(rootNode);
//            System.out.println(s);

            rootNode = new MerkleTreeV3<String>().deSerialize(s);
            s = new MerkleTreeV3<String>().serialize(rootNode);
//            System.out.println(s);
        }
}
