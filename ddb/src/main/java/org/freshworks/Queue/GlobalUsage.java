package org.freshworks.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GlobalUsage{

    public static ConcurrentLinkedQueue<String> writeQueue = new ConcurrentLinkedQueue<String>();

}
