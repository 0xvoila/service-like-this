package org.example;

public class Tree{

    int index = 0;
    static Node node;

    static class Node {
        int data;
        Node leftNode;
        Node rightNode;

        public Node(int x){
            this.data = x;
        }
    }

    public static Node deserialize(int[] pre_order, int index){

        if (index > pre_order.length - 1){
            return null;
        }

        Node node = new Node(pre_order[index]);

        if ( node.data == 0){
            return null;
        }

        index = index + 1;
        node.leftNode = deserialize(pre_order, index);

        index = index + 1;
        node.rightNode = deserialize(pre_order, index);

        return node;
    }

    public static void printTree(Node node){

        if(node == null){
            return;
        }
        else{
            System.out.println(node.data);
            printTree(node.leftNode);
            printTree(node.rightNode);
        }
    }

    public static void main(String args[]){
        Node node1 = Tree.deserialize(new int []{1,2,0,3}, 0);
        Tree.printTree(node1);
    }
}



