package org.system.amit.index;

import org.system.amit.lamport.Mutation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SSTable {

    String fileName = "";
    String minKey = "";
    String maxKey = "";

    public SSTable(String fileName){
        this.fileName = fileName;
    }

    public HashMap<String,String> flush(Memtable memtable){

        HashMap<String, String> x = new HashMap<String, String>();

        try{

            System.out.println("On the flush");
            minKey = memtable.RBTree.firstEntry().getKey();
            maxKey = memtable.RBTree.lastEntry().getKey();

            FileWriter fileOut = new FileWriter("data/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileOut);

            for ( Map.Entry<String, HashMap<String, Object>> entry :memtable.RBTree.entrySet()) {
                bufferedWriter.write(entry.getKey() + "," + entry.getValue());
                bufferedWriter.newLine();
            }

            x.put("min_key",minKey);
            x.put("max_key",maxKey);
            x.put("sstable",fileName);

            memtable = null;

            return x;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return x;
    }
}
