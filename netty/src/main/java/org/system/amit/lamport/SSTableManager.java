package org.system.amit.lamport;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SSTableManager {

    static ArrayList<HashMap<ArrayList<String>, String>> ssTables = new ArrayList<>();

    public void client(){

        try{

            while(true){
                Memtable memtable = Global.flushRBTree.poll();
                if ( memtable == null){
                    continue;
                }

                if (ssTables.size() > 1000){
                    // Flush ssTables to the disk for durability
                }
//                here create a new SSTable File and create the flush

                System.out.println("Got the memtable to flush");
                Random random=new Random();
                String ext = ".bin";
                File dir = new File("./");
                String name = String.format("%s%s",System.currentTimeMillis(),random.nextInt(100000)+ext);

                SSTable ssTable = new SSTable(name);
                HashMap<ArrayList<String>, String> y = new HashMap<>();
                y.put(ssTable.flush(memtable),name);
                ssTables.add(y);

            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Error in write path of the client");
        }
    }

//    public Mutation read(String key){
//
//        ArrayList<Mutation> listOfMutations = new ArrayList<>();
//
//        for (HashMap<ArrayList<String>, String> a: ssTables) {
//            HashMap.Entry<ArrayList<String>,String> entry = a.entrySet().iterator().next();
//            ArrayList<String> keys = entry.getKey();
//            String minKey = keys.get(0);
//            String maxKey = keys.get(1);
//            String fileName = entry.getValue();
//
////            Check if key is between minKey and maxKey
//            if (minKey.compareTo(key) < 0 && maxKey.compareTo(key) > 0){
////                Load the SSTable from disk and find the record. You may find it or you may not
//
//                FileInputStream fin = new FileInputStream(fileName);
//                ObjectInputStream objIn = new ObjectInputStream(fin);
//
//            }
//
//        }
//    }
}
