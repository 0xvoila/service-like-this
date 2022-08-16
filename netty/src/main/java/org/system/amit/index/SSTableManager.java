package org.system.amit.index;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

public class SSTableManager {

    static ArrayList<HashMap<String, String>> ssTables = new ArrayList<HashMap<String, String>>();

    public static void client(){

        try{

            while(true){
                Memtable memtable = Global.getInstance().flushRBTree.poll();
                if ( memtable == null){
//                    System.out.println("Queue is empty");
                    continue;
                }

                if (ssTables.size() > 10000){
                    // Flush ssTables to the disk for durability
                }
//                here create a new SSTable File and create the flush
                System.out.println("Flushing memtable");
                System.out.println("Queue size is " + Global.getInstance().flushRBTree.size());
                Random random=new Random();
                String dataExt = "_data.txt";
                String indexExt = "_index.txt";
                File dir = new File("./");
                String name = String.format("%s%s",System.currentTimeMillis(),random.nextInt(100000)+dataExt);

                String indexFileName = String.format("%s%s",System.currentTimeMillis(),random.nextInt(100000)+indexExt);

                SSTable ssTable = new SSTable(name,indexFileName);
                ssTables.add(ssTable.flush(memtable));

            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Error in write path of the client");
        }
    }

    public static String read(String key) throws IOException, ClassNotFoundException {

        String returnValue = null;

        if ( Global.getInstance().cache.get(key) != null){
            returnValue = Global.getInstance().cache.get(key);
            return returnValue;
        }

        for (HashMap<String, String> a: ssTables) {

            String minKey = a.get("min_key");
            String maxKey = a.get("max_key");

//            Check if key is between minKey and maxKey
            if (minKey.compareTo(key) < 0 && maxKey.compareTo(key) > 0){
//                Load the SSTable from disk and find the record. You may find it or you may not

                FileReader fin = new FileReader("data/" + a.get("sstable"));
                BufferedReader reader = new BufferedReader(fin);

                ObjectMapper mapper = new ObjectMapper();

                HashMap<String,Long> ssIndex = new HashMap<>();

                while(true){

                    String s = reader.readLine();

                    if (s == null){
                        break;
                    }
                    TypeReference<HashMap<String,Long>> typeRef
                            = new TypeReference<HashMap<String,Long>>() {};

                    Map.Entry<String, Long > x = mapper.readValue(s, typeRef).entrySet().iterator().next();
                    if ( x.getKey().equals(key)){
                        RandomAccessFile dataFile = new RandomAccessFile("data/" + a.get("dataFileName"),"r");
                        dataFile.seek(x.getValue());
                        returnValue = dataFile.readLine();
                        Global.getInstance().cache.put(x.getKey(), returnValue);
                    }
                }

            }
        }

        return returnValue;
    }
}
