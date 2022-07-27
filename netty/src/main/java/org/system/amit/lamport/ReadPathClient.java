package org.system.amit.lamport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.system.amit.lamport.DataStructure;
import org.system.amit.lamport.Mutation;

import java.util.Scanner;

public class ReadPathClient {

    public void client() {

        try{
            while(true){
                IMutation x = DataStructure.readQueue.poll();
                if ( x == null){
                    continue;
                }
                System.out.println(DataStructure.RBTree.get(x.getKey()));
//                Here get data from other replicas, reconcile the data and send update to the other servers

                  JavaClient javaClient = new JavaClient();
                  String[] peerNodes = {"127.0.0.1"};
                  javaClient.readPeerIMutation(peerNodes, x);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Error in read path of the client");
        }

    }

}
