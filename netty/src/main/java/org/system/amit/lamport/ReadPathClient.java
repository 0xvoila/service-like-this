package org.system.amit.lamport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class ReadPathClient {

    HashMap<String, Integer> peerNodes = new HashMap<>();

    public void client() {

        try{
            while(true){
                Mutation x = Global.readQueue.poll();
                if ( x == null){
                    continue;
                }

                if (!MemtableManager.containsKey(x.getKey())){
                    System.out.println("Key does not exists in the red black tree");
                    x.setValue(null);
                    x.setTimestamp(-1);
                }
                else{
                    System.out.println("Key exists in the read queue");
                    x = MemtableManager.read(x.getKey());
                }

//                Here get data from other replicas, reconcile the data and send update to the other servers

                InternodeClient internodeClient = new InternodeClient();
                if (peerNodes.isEmpty()){
                    System.out.println("Peer Nodes are empty yet");
                    // This is just the make shift, where we are asking user for its peer. However it should happen automatically
                    Scanner scanner = new Scanner(System.in);
                    while(true){
                        System.out.println("Enter the host and port of the peer to connect to, type END when all nodes are ended");
                        String input = scanner.nextLine();

                        if (input.equals("END")){
                            break;
                        }
                        String host = input.split(",")[0];
                        System.out.println("Host is " + host);

                        int port = Integer.parseInt(input.split(",")[1]);
                        System.out.println("Port is " + port);
                        peerNodes.put(host,port);
                    }

                }
                else{
                    System.out.println("Peer Nodes are NOT empty yet");
                    System.out.println(peerNodes);
                }

                ArrayList<Mutation> peerResponse = internodeClient.readPeerIMutation(peerNodes, x);
                peerResponse.sort(Comparator.comparingInt(Mutation::getTimestamp));
                if ( !peerResponse.isEmpty()){
                    Mutation mutation = peerResponse.get(0);
                    if (mutation.getTimestamp() > x.getTimestamp()){
                        Global.lamport_counter = mutation.getTimestamp() + 1;
                        mutation.setTimestamp(Global.lamport_counter);
                        System.out.println("Latest Read Value : " + mutation.getValue());
                        Global.writeQueue.add(mutation);
                    }
                    else{
                        System.out.println("Latest Read Value : " + MemtableManager.read(x.getKey()).getValue());
                    }

                }
                else{
                    System.out.println("Latest Read Value : " + MemtableManager.read(x.getKey()).getValue());
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
