package org.system.amit.lamport;
import com.google.common.util.concurrent.*;
import org.checkerframework.checker.units.qual.A;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JavaClient {

    String NODE_NAME;
    ArrayList<Mutation> responseList = new ArrayList<>();

    public JavaClient(String nodeName){
        this.NODE_NAME = nodeName;
    }

    public JavaClient(){

    }

    public void setResponseList(Mutation o){
        synchronized (this){
            this.responseList.add(o);
        }
    }
    public ArrayList<Mutation> readPeerIMutation (String[] hosts ,IMutation iMutation ) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(hosts.length);
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

        for (String node: hosts) {
            ListenableFuture<Mutation> x = listeningExecutorService.submit(() -> client(iMutation, node));
            Futures.addCallback(x, new FutureCallback<Mutation>() {

                @Override
                public void onSuccess(Mutation o) {
                    setResponseList(o);
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            }, executorService);
        }

        if (!listeningExecutorService.awaitTermination(1000, TimeUnit.SECONDS)){
            listeningExecutorService.shutdownNow();
        }

        return responseList;
    }

    public Mutation client(IMutation iMutation, String host){

        Socket socket = null;
        Mutation response = null;
        ObjectInputStream objectInputStreamReader = null;
        ObjectOutputStream objectOutputStreamWriter = null;

        try {
            socket = new Socket(host, 5000);
            objectInputStreamReader = new ObjectInputStream(socket.getInputStream());
            objectOutputStreamWriter = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStreamWriter.writeObject(iMutation);
            objectOutputStreamWriter.writeObject("\n");
            objectOutputStreamWriter.flush();

            while (true){
                response = (Mutation) objectInputStreamReader.readObject();

                if ( response != null){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
            }
        }

        return response;
    }
}