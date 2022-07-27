package org.system.amit.lamport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class WritePathClient {

    public void client(){

        try{
            while(true){
                Mutation x = DataStructure.writeQueue.poll();
                if ( x == null){
                    continue;
                }

                if (x.getCommand().equals("DELETE")){
                    DataStructure.RBTree.put(x.getKey(), x.getValue() + "," + "DELETED");
                }
                else{
                    DataStructure.RBTree.put(x.getKey(), x.getValue());
                }

            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Error in write path of the client");
        }
    }
}
