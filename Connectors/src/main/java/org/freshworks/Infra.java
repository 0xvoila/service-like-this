package org.freshworks;

import org.freshworks.core.model.DiscoveryObject;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Infra {

    public static LinkedBlockingQueue<String>  kafka = new LinkedBlockingQueue<String>();
}
