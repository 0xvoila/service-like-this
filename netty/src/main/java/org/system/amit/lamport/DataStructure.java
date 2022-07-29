package org.system.amit.lamport;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class DataStructure {

    public static ConcurrentLinkedQueue<Mutation> writeQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Mutation> readQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentSkipListMap<String, Mutation> RBTree = new ConcurrentSkipListMap<>();

    public static HashMap<String, Integer> peerList = new HashMap<>();

    public static int lamport_counter = 0;

}
