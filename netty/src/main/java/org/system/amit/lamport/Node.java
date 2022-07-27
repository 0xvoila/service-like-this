package org.system.amit.lamport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Node extends Thread{

    public static void main(String args[]){

        try{

            Thread thInput = new Thread(() -> Node.insert());
            thInput.start();

            JavaServer server = new JavaServer(args[0], args[1], Integer.parseInt(args[2]));
            Thread thServer = new Thread(() -> {
                try {
                    server.startServer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thServer.start();

            // Enter data using BufferReader
//            System.out.println("Enter the host and port to which this client connects to. Make sure to start the another server before this");
//            BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));
//            // Reading data using readLine
//            String input = reader.readLine();
//            String[] x = input.split(" ");
            WritePathClient writePathClient = new WritePathClient();
            Thread thWritePathClient =  new Thread(() -> writePathClient.client());
            thWritePathClient.start();

            ReadPathClient readPathClient = new ReadPathClient();
            Thread thReadPathClient =  new Thread(() -> readPathClient.client());
            thReadPathClient.start();

//            thServer.join();
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
        int lamport_counter = 0;

        while(scanner.hasNext()){
            String record = scanner.nextLine();
            lamport_counter = lamport_counter + 1;
            String[] recordKeyValue = record.split(",",2);

            if ( recordKeyValue[0].equals("WRITE") || recordKeyValue[0].equals("UPDATE") || recordKeyValue[0].equals("DELETE")){
                recordKeyValue = record.split(",",3);
                Mutation mutation = new Mutation(recordKeyValue[0], recordKeyValue[1], recordKeyValue[2], lamport_counter);
                DataStructure.writeQueue.add(mutation);
            }
            else {
                recordKeyValue = record.split(",",2);
                IMutation iMutation = new IMutation(recordKeyValue[0], recordKeyValue[1], lamport_counter);
                DataStructure.readQueue.add(iMutation);
            }

        }
    }

}
