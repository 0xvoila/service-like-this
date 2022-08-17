package org.system.amit.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SSTable {

    String dataFileName = "";

    String indexFileName = "";

    String bloomFileName = "";

    String minKey = "";
    String maxKey = "";

    public SSTable(String dataFileName, String indexFileName){
        this.dataFileName = dataFileName;
        this.indexFileName = indexFileName;
    }

    public HashMap<String,Object> flush(Memtable memtable){

        HashMap<String, Object> x = new HashMap<String, Object>();


        try{

            BloomFilter<String> filter = BloomFilter.create(
                    Funnels.stringFunnel(Charset.forName("UTF-8")), 100000, 0.01);

            minKey = memtable.RBTree.firstEntry().getKey();
            maxKey = memtable.RBTree.lastEntry().getKey();

            FileWriter fileOut = new FileWriter(Global.getInstance().databaseDirectory + dataFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileOut);


            FileWriter fileOutIndex = new FileWriter(Global.getInstance().databaseDirectory + indexFileName);
            BufferedWriter objectOutIndex = new BufferedWriter(fileOutIndex);

            long offsetCounter = 0;

            System.out.println("Size of memtable is " + memtable.RBTree.size());
            for ( Map.Entry<String, HashMap<String, Object>> entry :memtable.RBTree.entrySet()) {

                bufferedWriter.write(entry.getKey() + "," + entry.getValue().toString());
                bufferedWriter.newLine();

                filter.put(entry.getKey());

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
            x.put("bloom_filter", filter);


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
