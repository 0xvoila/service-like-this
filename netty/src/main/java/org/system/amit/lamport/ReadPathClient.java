package org.system.amit.lamport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class ReadPathClient {

    public void client() {

        try{
            while(true){
                Mutation x = DataStructure.readQueue.poll();
                if ( x == null){
                    continue;
                }

                if (!DataStructure.RBTree.containsKey(x.getKey())){
                    x.setValue(null);
                    x.setTimestamp(-1);
                }
                else{
                    x = DataStructure.RBTree.get(x.getKey());
                }

//                Here get data from other replicas, reconcile the data and send update to the other servers

                InternodeClient internodeClient = new InternodeClient();
                HashMap<String, Integer> peerNodes = new HashMap<>();

//                This is just the make shift, where we are asking user for its peer. However it should happen automatically
                System.out.println("Enter the host and port of the peer to connect to");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();

                String host = input.split(",")[0];
                System.out.println("Host is " + host);

                int port = Integer.parseInt(input.split(",")[1]);
                System.out.println("Port is " + port);
                peerNodes.put(host,port);


                System.out.println(peerNodes);
                ArrayList<Mutation> peerResponse = internodeClient.readPeerIMutation(peerNodes, x);
                System.out.println("Peer response is " + peerResponse.toString());
                peerResponse.sort(Comparator.comparingInt(Mutation::getTimestamp));
                if ( !peerResponse.isEmpty()){
                    Mutation mutation = peerResponse.get(0);
                    if (mutation.getTimestamp() > x.getTimestamp()){
                        DataStructure.lamport_counter = mutation.getTimestamp() + 1;
                        mutation.setTimestamp(DataStructure.lamport_counter);
                        System.out.println("Latest Read Value : " + mutation.getValue());
                        DataStructure.writeQueue.add(mutation);
                    }
                    else{
                        System.out.println("Latest Read Value : " + DataStructure.RBTree.get(x.getKey()).getValue());
                    }

                }
                else{
                    System.out.println("Latest Read Value : " + DataStructure.RBTree.get(x.getKey()).getValue());
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Error in read path of the client");
        }

    }

}
