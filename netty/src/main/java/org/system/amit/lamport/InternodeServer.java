package org.system.amit.lamport;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InternodeServer {

    String NODE_NAME;
    String HOST;
    int PORT;
    public InternodeServer(String nodeName, String host, int port){

        this.NODE_NAME = nodeName;
        this.HOST = host;
        this.PORT = port;
    }

    public void startServer() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        ServerSocket serversocket ;
        Socket socket ;

        serversocket = new ServerSocket(this.PORT);

        while(true){
            socket = serversocket.accept();

            System.out.println("Client is connected");

            Socket clientSocket = socket;
            executorService.submit(new Runnable() {
                @Override
                public void run() {

                    ObjectInputStream inputStreamReader ;
                    ObjectOutputStream outputStreamWriter ;
                    try {

                        outputStreamWriter = new ObjectOutputStream(clientSocket.getOutputStream());
                        inputStreamReader = new ObjectInputStream(clientSocket.getInputStream());
                        Mutation x = (Mutation)inputStreamReader.readObject();
                        Mutation mutation = Global.RBTree.get(x.getKey());

                        if (mutation != null){
                            if (x.getValue() == null){
                                Global.lamport_counter = mutation.getTimestamp() + 1;
                                outputStreamWriter.writeObject(mutation);
                                outputStreamWriter.flush();
                                clientSocket.close();
                            }
                            else if (x.getTimestamp() > mutation.getTimestamp()){
                                Global.lamport_counter = x.getTimestamp() + 1;
                                Global.writeQueue.add(x);
                                outputStreamWriter.writeObject(mutation);
                                outputStreamWriter.flush();
                                clientSocket.close();
                            }
                        }
                        else{
                            Global.lamport_counter = x.getTimestamp() + 1;
                            Global.writeQueue.add(x);
                            outputStreamWriter.writeObject(null);
                            outputStreamWriter.flush();
                            clientSocket.close();
                        }

                    } catch (Exception e) {
                        System.out.println("Exception occured " );
                        e.printStackTrace();
                        return;
                    }

                }
            });
        }
    }
}
