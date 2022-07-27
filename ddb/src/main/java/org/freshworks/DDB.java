package org.freshworks;

import org.freshworks.Queue.DDBConsumer;
import org.freshworks.net.DDBServer;

public class DDB {

    public static void main(String args[]){

        new DDBServer().DDBServerStart();
        new DDBConsumer().DDBConsumerStart();
    }
}
