package org.system.amit.index;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.BloomFilter;

import java.io.*;
import java.util.*;

public class SSTableManager {

    static ArrayList<HashMap<String, Object>> ssTables = new ArrayList<HashMap<String, Object>>();

    public static void loadConfig(String databaseName) throws IOException, ClassNotFoundException {

        Global.getInstance().setDatabaseDirectory("data/" + databaseName + "/");
        File dir = new File( Global.getInstance().databaseDirectory);
        if (!dir.exists()) dir.mkdirs();
        File f = new File( Global.getInstance().databaseDirectory +  "ss_tables.txt");
        if (f.exists()){
            FileInputStream fin
                    = new FileInputStream(Global.getInstance().databaseDirectory + "ss_tables.txt");

            ObjectInputStream oin
                    = new ObjectInputStream(fin);
            ssTables = (ArrayList<HashMap<String, Object>>)oin.readObject();
        }
    }
    public static void client(){

        try{

            while(true){
                Memtable memtable = Global.getInstance().flushRBTree.poll();
                if ( memtable == null){
//                    System.out.println("Queue is empty");
                    continue;
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

                FileOutputStream fos
                        = new FileOutputStream(Global.getInstance().databaseDirectory + "ss_tables.txt");

                ObjectOutputStream oos
                        = new ObjectOutputStream(fos);
                oos.writeObject(ssTables);

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

        for (HashMap<String, Object> a: ssTables) {

            String minKey = (String)a.get("min_key");
            String maxKey = (String)a.get("max_key");
            BloomFilter<String> filter = (BloomFilter<String>)a.get("bloom_filter");

            System.out.println("Bloom says " + filter.mightContain(key));
//            Check if key is between minKey and maxKey
            if (minKey.compareTo(key) < 0 && maxKey.compareTo(key) > 0 && filter.mightContain(key)){
//                Load the SSTable from disk and find the record. You may find it or you may not

                FileReader fin = new FileReader(Global.getInstance().databaseDirectory + a.get("sstable"));
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
                        RandomAccessFile dataFile = new RandomAccessFile(Global.getInstance().databaseDirectory + a.get("dataFileName"),"r");
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
