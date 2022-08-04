package org.system.amit.lamport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class Global {

    public static ConcurrentLinkedQueue<Mutation> writeQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Mutation> readQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentSkipListMap<String, Mutation> RBTree = new ConcurrentSkipListMap<>();

    public static HashMap<String, Map<String, Object>> peerList = new HashMap<>();
    public static int lamport_counter = 0;

    public static ConcurrentLinkedQueue<Memtable> flushRBTree = new ConcurrentLinkedQueue<>();

}
