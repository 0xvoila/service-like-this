package org.system.amit.index;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class SSTableManager {

    static ArrayList<HashMap<String, String>> ssTables = new ArrayList<HashMap<String, String>>();

    public void client(){

        try{

            while(true){
                Memtable memtable = Global.flushRBTree.poll();
                if ( memtable == null){
                    continue;
                }

                if (ssTables.size() > 1000000){
                    // Flush ssTables to the disk for durability
                }
//                here create a new SSTable File and create the flush

                System.out.println("Got the memtable to flush");
                Random random=new Random();
                String ext = ".txt";
                File dir = new File("./");
                String name = String.format("%s%s",System.currentTimeMillis(),random.nextInt(100000)+ext);

                SSTable ssTable = new SSTable(name);
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

    public static String read(String key) throws IOException {

        String returnValue = null;

        for (HashMap<String, String> a: ssTables) {

            String minKey = a.get("min_key");
            String maxKey = a.get("max_key");

//            Check if key is between minKey and maxKey
            if (minKey.compareTo(key) < 0 && maxKey.compareTo(key) > 0){
//                Load the SSTable from disk and find the record. You may find it or you may not

                FileReader fin = new FileReader("data/" + a.get("sstable"));
                BufferedReader reader = new BufferedReader(fin);

                while(true){

                    String input = reader.readLine();
                    if (input == null){
                        break;
                    }
                    String[] recordKey = input.split(",",2);

                    if (recordKey[0].equals(key)){
                        System.out.println("value found is " + recordKey[1]);
                        returnValue = recordKey[1];
                    }
                } // while close

            }
        }

        return returnValue;
    }
}
