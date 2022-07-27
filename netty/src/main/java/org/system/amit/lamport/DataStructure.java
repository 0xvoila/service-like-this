package org.system.amit.lamport;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataStructure {

    public static ConcurrentLinkedQueue<Mutation> writeQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<IMutation> readQueue = new ConcurrentLinkedQueue<>();
    public static TreeMap<String, String> RBTree = new TreeMap<>();
}
