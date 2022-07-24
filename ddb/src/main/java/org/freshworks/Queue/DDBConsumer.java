package org.freshworks.Queue;

import org.freshworks.Queue.GlobalUsage;

public class DDBConsumer {

    public DDBConsumer(){

    }

    public void DDBConsumerStart(){

        try{
            while(true){
                String msg = GlobalUsage.writeQueue.poll();

                if(msg != null){
                    System.out.println("Got message from queue");

//                    Here Write to WAL File
//                    Then write to mem tables
                }
            }
        }
        catch(Exception e){
            System.out.println("Global queue consumer terminates with reason " + e.getMessage());
        }

    }
}
