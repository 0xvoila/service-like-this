package org.system.amit.index;

import java.util.HashMap;

public class MemtableManager {

    private static Memtable memtable;

    public static void write(String key, HashMap<String, Object> mutation) {
        if (memtable == null || memtable.getStatus() != null) {
            System.out.println("memtable is null");
            memtable = new Memtable();
            memtable.write(key, mutation);
        }

        else if (memtable.getSize() > 10000){
            System.out.println("mem table is full");
            memtable.markQueuedForFlush();
            Global.flushRBTree.add(memtable);
            memtable = new Memtable();
            memtable.write(key, mutation);
        }
        else{
            memtable.write(key, mutation);
        }
    }

    public static HashMap<String, Object> read(String key){

        if (memtable == null){
            return null;
        }
        else{
            return memtable.read(key);
        }
    }

    public static Boolean containsKey(String key){

        if (memtable == null){
            return false;
        }
        else{
            return memtable.containsKey(key);
        }
    }

}
