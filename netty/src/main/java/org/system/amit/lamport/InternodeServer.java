package org.system.amit.lamport;
import javax.xml.crypto.Data;
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
                        Mutation mutation = DataStructure.RBTree.get(x.getKey());

                        if (mutation != null){
                            if (x.getValue() == null){
                                DataStructure.lamport_counter = mutation.getTimestamp() + 1;
                                outputStreamWriter.writeObject(mutation);
                                outputStreamWriter.flush();
                                clientSocket.close();
                            }
                            else if (x.getTimestamp() > mutation.getTimestamp()){
                                DataStructure.lamport_counter = x.getTimestamp() + 1;
                                DataStructure.writeQueue.add(x);
                                outputStreamWriter.writeObject(mutation);
                                outputStreamWriter.flush();
                                clientSocket.close();
                            }
                        }
                        else{
                            DataStructure.lamport_counter = x.getTimestamp() + 1;
                            DataStructure.writeQueue.add(x);
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
