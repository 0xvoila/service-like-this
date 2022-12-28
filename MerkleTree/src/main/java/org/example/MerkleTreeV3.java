package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;


public class MerkleTreeV3<T>{

    static ObjectMapper objectMapper = new ObjectMapper();

    static class Node<T>{

            T data;
            long hash;
            Node<T> leftNode;
            Node<T> rightNode;

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

            public Boolean isLeaf(){
                if (this.leftNode == null && this.rightNode == null){
                    return true;
                }
                else{
                    return false;
                }
            }

            public Boolean isRoot(){

                if (this.parentNode == null){
                    return true;
                }
                else{
                    return false;
                }
            }

            public Node<T> getParentNode() {
                return parentNode;
            }

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

        public String serialize(Node<T> rootNode) {

            ArrayList<Node<T>> preOrderedList;

            try{
                preOrderedList = _serialize(rootNode, new ArrayList<>());
                return objectMapper.writeValueAsString(preOrderedList);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }

            return null;
        }

        private  ArrayList<Node<T>> _serialize(Node<T> rootNode, ArrayList<Node<T>> preOrderList){

            if(rootNode == null){
                preOrderList.add(new Node<>(0));
                return null;
            }
            else {
                preOrderList.add(rootNode);
                _serialize(rootNode.leftNode , preOrderList);
                _serialize(rootNode.rightNode , preOrderList);
            }

            return preOrderList;
        }

        public  Node<T> deSerialize(String str){

            try{
                TypeReference<ArrayList<Node<T>>> typeRef = new TypeReference<ArrayList<Node<T>>>() {};
                ArrayList<Node<T>> preOrderedList = objectMapper.readValue(str, typeRef);

                Node<T> rootNode = new Node<>();

                for (Node<T> tNode : preOrderedList) {
                    rootNode = _deserialize(tNode);
                }

                return rootNode;
            }
            catch(Exception exception){
                System.out.println(exception.getMessage());
            }
            return null;
        }

        public Node<T> _deserialize(Node<T> node){

            if (node.getHash() == 0){
                return null;
            }
            else{
                node.leftNode = _deserialize(node.leftNode);
                node.rightNode = _deserialize(node.rightNode);
            }
            return node;
        }
}
