package org.system.amit.index;

import org.system.amit.lamport.WritePathClient;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;


public class FileIndex {

    static HashMap<String, Long>  index = new HashMap<>();

    public static void main(String args[]) throws IOException, InterruptedException {

        SSTableManager ssTableManagerClient = new SSTableManager();
        Thread thSSTableManagerClient =  new Thread(() -> ssTableManagerClient.client());

        Thread createIndexTh = new Thread(()-> {
            try {
                createIndex("data/database.csv");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        Thread searchTh = new Thread(()-> {
            try {
                search("data/database.csv");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        createIndexTh.start();
        thSSTableManagerClient.start();
        searchTh.start();

        createIndexTh.join();
        thSSTableManagerClient.join();
        searchTh.join();
    }

    public static void createIndex(String fileName) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String[] headerArray = reader.readLine().split(",");
        HashMap<String, Object> map = new HashMap<>();

        String input = "";
        while(true){

            input = reader.readLine();
            if ( input == null){
                break;
            }
            String[] inputArray = input.split(",");
            String [] x = input.split(",",2);

            for(int i=0; i<headerArray.length; i++){
                map.put(headerArray[i], inputArray[i]);
            }

            MemtableManager.write(x[0], map);
        }
    }

    public static void search(String filename) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(true){

            System.out.println("Enter the key to search for");
            String key = scanner.nextLine();

            if (key.equals("BYE")){
                break;
            }

            System.out.println("Key is " + key);
            if (MemtableManager.read(key) != null){
                System.out.println(MemtableManager.read(key));
            }
            else {
                System.out.println(SSTableManager.read(key));
            }


        }
    }
}
