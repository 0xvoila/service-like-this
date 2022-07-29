package org.system.amit.lamport;
import com.google.common.util.concurrent.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InternodeClient {

    String NODE_NAME;
    ArrayList<Mutation> responseList = new ArrayList<>();

    public InternodeClient(String nodeName){
        this.NODE_NAME = nodeName;
    }

    public InternodeClient(){

    }

    public void setResponseList(Mutation o){
        synchronized (this){
            this.responseList.add(o);
        }
    }
    public ArrayList<Mutation> readPeerIMutation (HashMap<String, Integer> peerNodes , Mutation mutation ) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(peerNodes.size() + 10);
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

        for (Map.Entry<String, Integer> node: peerNodes.entrySet()) {
            ListenableFuture<Mutation> x = listeningExecutorService.submit(() -> client(mutation, node.getKey(), node.getValue()));
            Futures.addCallback(x, new FutureCallback<Mutation>() {

                @Override
                public void onSuccess(Mutation o) {
                    setResponseList(o);
                    listeningExecutorService.shutdown();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    listeningExecutorService.shutdown();
                }
            }, listeningExecutorService);
        }

        if (!listeningExecutorService.awaitTermination(100, TimeUnit.SECONDS)){
            listeningExecutorService.shutdownNow();
        }

        return responseList;
    }

    public Mutation client(Mutation mutation, String peerHost, int peerPort){

        Socket socket = null;
        Mutation response = null;
        ObjectInputStream objectInputStreamReader = null;
        ObjectOutputStream objectOutputStreamWriter = null;

        try {
            socket = new Socket(peerHost, peerPort);
            objectOutputStreamWriter = new ObjectOutputStream(socket.getOutputStream());
            objectInputStreamReader = new ObjectInputStream(socket.getInputStream());
            objectOutputStreamWriter.writeObject(mutation);
            objectOutputStreamWriter.flush();

            System.out.println("Reading input stream from server");
            response = (Mutation) objectInputStreamReader.readObject();
            System.out.println("Mutation is " + response.toString());

            return  response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        } finally {
            try {
                if (socket != null)
                    socket.close();
                if (objectInputStreamReader != null)
                    objectInputStreamReader.close();
                if (objectOutputStreamWriter != null)
                    objectOutputStreamWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}