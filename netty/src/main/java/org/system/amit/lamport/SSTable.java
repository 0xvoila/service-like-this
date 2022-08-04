package org.system.amit.lamport;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

public class SSTable {

    String fileName = "";
    String minKey = "";
    String maxKey = "";

    public SSTable(String fileName){
        this.fileName = fileName;
    }

    public ArrayList<String> flush(Memtable memtable){

        ArrayList<String> x = new ArrayList<String>();

        try{

            System.out.println("On the flush");
            minKey = memtable.RBTree.firstEntry().getValue().getKey();
            maxKey = memtable.RBTree.lastEntry().getValue().getKey();

            System.out.println("On the flush " + minKey);
            System.out.println("On the flush " + maxKey);

            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            for ( Map.Entry<String, Mutation> entry :memtable.RBTree.entrySet()) {
                objectOut.writeObject(entry);
            }

            x.add(minKey);
            x.add(maxKey);

            return x;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return x;
    }
}
