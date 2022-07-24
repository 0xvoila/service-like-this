package org.system.amit.lamport;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Node extends Thread{

    public static void main(String args[]){

        try{
            Server1 server1 = new Server1(args[0], args[1], Integer.parseInt(args[2]));
            Thread thServer = new Thread(() -> server1.startServer());
            thServer.start();

            // Enter data using BufferReader
            System.out.println("Enter the host and port to which this client connects to. Make sure to start the another server before this");
            BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));
            // Reading data using readLine
            String input = reader.readLine();
            String[] x = input.split(" ");
            Client client = new Client();
            Thread thClient =  new Thread(() -> client.startClient(x[0], Integer.parseInt(x[1])));
            thClient.start();

            thServer.join();
            thClient.join();
            System.out.println("I am here");

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {

        }

    }

}
