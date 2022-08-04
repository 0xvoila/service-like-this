package org.system.amit.lamport;

import java.util.concurrent.ConcurrentSkipListMap;

public class Memtable {

    int size = 0;
    Status status = null;
    enum Status {
        FLUSH_STARTED,
        UNDER_FLUSH,
        FLUSH_ENDED
    };

    public ConcurrentSkipListMap<String, Mutation> RBTree = new ConcurrentSkipListMap<>();

    public void write(String key, Mutation mutation){
        this.RBTree.put(key, mutation);
    }

    public Mutation read(String key){
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
