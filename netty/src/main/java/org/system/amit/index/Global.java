package org.system.amit.index;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class Global {

    public static ConcurrentLinkedQueue<Memtable> flushRBTree = new ConcurrentLinkedQueue<>();

}
