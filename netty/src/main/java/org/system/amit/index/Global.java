package org.system.amit.index;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class Global {

    private static Global global_instance = null;

    public ConcurrentLinkedQueue<Memtable> flushRBTree = null;
    public HashMap<String, String> cache = null;

    private Global(){
        this.flushRBTree = new ConcurrentLinkedQueue<>();
        this.cache = new HashMap<>();
    }
    public static Global getInstance(){
        if ( global_instance == null){
            global_instance = new Global();
        }
        return global_instance;
    }
}
