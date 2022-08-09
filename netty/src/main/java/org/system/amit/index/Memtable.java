package org.system.amit.index;

import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Memtable {

    int size = 0;
    Status status = null;
    enum Status {
        FLUSH_STARTED,
        UNDER_FLUSH,
        FLUSH_ENDED
    };

    public ConcurrentSkipListMap<String, HashMap<String, Object>> RBTree = new ConcurrentSkipListMap<>();

    public void write(String key, HashMap<String, Object> mutation){
        this.RBTree.put(key, mutation);
    }

    public HashMap<String, Object> read(String key){
        return this.RBTree.get(key);
    }

    public Boolean containsKey(String key){
        return this.RBTree.containsKey(key);
    }

    public int getSize(){

        return this.RBTree.size();
    }

    public Status getStatus(){
        return this.status;
    }

    public void markQueuedForFlush(){
        status = Status.FLUSH_STARTED;
    }

    public void markUnderFlush(){
        status = Status.UNDER_FLUSH;
    }

    public void markFlushed(){
        status = Status.FLUSH_ENDED;
    }
}
