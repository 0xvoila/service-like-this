package org.system.amit.lamport;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaServer {

    String NODE_NAME;
    String HOST;
    int PORT;
    public JavaServer(String nodeName, String host, int port){

        this.NODE_NAME = nodeName;
        this.HOST = host;
        this.PORT = port;
    }

    public void startServer() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        ServerSocket serversocket ;
        Socket socket ;

        serversocket = new ServerSocket(5000);

        while(true){
            socket = serversocket.accept();
            Socket clientSocket = socket;
            executorService.submit(new Runnable() {
                @Override
                public void run() {

                    InputStreamReader inputStreamReader ;
                    OutputStreamWriter outputStreamWriter ;
                    BufferedReader bufferedReader ;
                    BufferedWriter bufferedWriter ;

                    while (true) {
                        try {

                            inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                            outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
                            bufferedReader = new BufferedReader(inputStreamReader);
                            bufferedWriter = new BufferedWriter(outputStreamWriter);

                            while (true){
                                String msgFromClient = bufferedReader.readLine();
                                System.out.println("Client: " + msgFromClient);
                                bufferedWriter.write(" MSG Received");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                if (msgFromClient.equalsIgnoreCase("BYE"))
                                    break;
                            }
                            clientSocket.close();
                            inputStreamReader.close();
                            outputStreamWriter.close();
                            bufferedReader.close();
                            bufferedWriter.close();

                        } catch (Exception e) {
                            System.out.println("Exception occured " + e.getMessage());
                            return;
                        }

                    } // while close
                }
            });
        }
    }
}
