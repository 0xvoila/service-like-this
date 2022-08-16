package org.system.amit.index;

import org.system.amit.lamport.WritePathClient;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class FileIndex {

    static HashMap<String, Long>  index = new HashMap<>();

    public static void main(String args[]) throws IOException, InterruptedException {

//        memoryFiller();

        ExecutorService service = Executors.newFixedThreadPool(100);

        for(int i=0; i<50; i++){
            service.submit(() -> SSTableManager.client());
        }

        Thread createIndexTh = new Thread(()-> {
            try {
                createPrimaryIndex("data/database.csv");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        Thread searchTh = new Thread(()-> {
            try {
                search("data/database.csv");
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        createIndexTh.start();
        searchTh.start();

        createIndexTh.join();
        searchTh.join();

        service.shutdown();
        service.awaitTermination(10000, TimeUnit.DAYS);
    }

    public static void memoryFiller(){

        for(int i=0; i<10000000; i++){
            String m = "R2cIFCqKIL087tbV1pLpDYMjjgh6SrXMkeV7JrXVw8k0TiKXEPcEhtMve26aYysqKft1o9R2db3YN0yono6fwiJH4xG7WqBjqCO6";
            System.out.println(m.length());
            System.out.println(i);
        }

    }
    public static void createPrimaryIndex(String fileName) throws IOException, InterruptedException {


        long startTime = System.currentTimeMillis();


        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String[] headerArray = reader.readLine().split(",");

        String input = "";
        while(true){

//            Thread.sleep(1);
            HashMap<String, Object> map = new HashMap<>();
            input = reader.readLine();
            if ( input == null){
                break;
            }

            String[] y = input.split(",");

            for(int i=0; i<headerArray.length; i++){
                map.put(headerArray[i], y[i]);
            }

//            Here assuming first field would be the primary field in the record.
//            It can be customized as per requirement

            MemtableManager.write(input.split(",",2)[0], map);

        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time elapsed is");
        System.out.println(elapsedTime);
    }

    public static void search(String filename) throws IOException, ClassNotFoundException {
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
