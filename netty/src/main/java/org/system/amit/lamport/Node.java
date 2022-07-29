package org.system.amit.lamport;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Node extends Thread{

    public static void main(String args[]){

        try{

            Thread thInput = new Thread(() -> Node.insert());
            thInput.start();

            InternodeServer server = new InternodeServer(args[0], args[1], Integer.parseInt(args[2]));
            Thread thServer = new Thread(() -> {
                try {
                    server.startServer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thServer.start();

            WritePathClient writePathClient = new WritePathClient();
            Thread thWritePathClient =  new Thread(() -> writePathClient.client());
            thWritePathClient.start();

            ReadPathClient readPathClient = new ReadPathClient();
            Thread thReadPathClient =  new Thread(() -> readPathClient.client());
            thReadPathClient.start();

            thServer.join();
            thWritePathClient.join();
            thReadPathClient.join();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {

        }

    }

    public static void insert(){

        Scanner scanner = new Scanner(System.in);

        while(scanner.hasNext()){
            String record = scanner.nextLine();
            String[] recordKeyValue = record.split(",",2);

            if ( recordKeyValue[0].equals("WRITE") || recordKeyValue[0].equals("UPDATE") || recordKeyValue[0].equals("DELETE")){
                DataStructure.lamport_counter = DataStructure.lamport_counter + 1;
                recordKeyValue = record.split(",",3);
                Mutation mutation = new Mutation(recordKeyValue[0], recordKeyValue[1], recordKeyValue[2], DataStructure.lamport_counter);
                DataStructure.writeQueue.add(mutation);
            }
            else if(recordKeyValue[0].equals("READ")) {
                recordKeyValue = record.split(",",2);
                Mutation mutation = new Mutation();
                mutation.setCommand(recordKeyValue[0]);
                mutation.setKey(recordKeyValue[1]);
                mutation.setValue(null);
                DataStructure.readQueue.add(mutation);
            }

            else if (recordKeyValue[0].equals("Show table")){

                for(Map.Entry<String, Mutation> entrySet: DataStructure.RBTree.entrySet())
                    System.out.println(entrySet.getKey() + " " + entrySet.getValue().getValue() + " " + entrySet.getValue().getTimestamp() + " " + entrySet.getValue().getTombstone());
            }

        }
    }

}
