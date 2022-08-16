package org.system.amit.index;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SSTable {

    String dataFileName = "";

    String indexFileName = "";

    String minKey = "";
    String maxKey = "";

    public SSTable(String dataFileName, String indexFileName){
        this.dataFileName = dataFileName;
        this.indexFileName = indexFileName;
    }

    public HashMap<String,String> flush(Memtable memtable){

        HashMap<String, String> x = new HashMap<String, String>();


        try{

            minKey = memtable.RBTree.firstEntry().getKey();
            maxKey = memtable.RBTree.lastEntry().getKey();

            FileWriter fileOut = new FileWriter("data/" + dataFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileOut);

            FileWriter fileOutIndex = new FileWriter("data/" + indexFileName);
            BufferedWriter objectOutIndex = new BufferedWriter(fileOutIndex);

            long offsetCounter = 0;

            System.out.println("Size of memtable is " + memtable.RBTree.size());
            for ( Map.Entry<String, HashMap<String, Object>> entry :memtable.RBTree.entrySet()) {

                bufferedWriter.write(entry.getKey() + "," + entry.getValue().toString());
                bufferedWriter.newLine();

//                index.put(entry.getKey(), offsetCounter);
                objectOutIndex.write("{" + '"' + entry.getKey() + '"' + ":" + offsetCounter + "}");
                objectOutIndex.newLine();

                String record = entry.getKey() + "," + entry.getValue();
                offsetCounter = offsetCounter + record.length() + 1 ;
            }
            System.out.println("Memtable is written in file");
//
            bufferedWriter.close();
            fileOut.close();

            objectOutIndex.close();
            fileOutIndex.close();

            x.put("min_key",minKey);
            x.put("max_key",maxKey);
            x.put("sstable", indexFileName);
            x.put("dataFileName", dataFileName);


            memtable = null;
            System.out.println("Table is flushed");
            return x;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return x;
    }
}
