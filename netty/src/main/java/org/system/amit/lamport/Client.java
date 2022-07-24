package org.system.amit.lamport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.system.amit.echo.FileMessage;

import java.io.FileWriter;
import java.util.Scanner;

public class Client {
    static int lamport_counter = 0;

//    public static void main(String args[]){
//
//        Client client = new Client();
//        client.startClient(args[0],Integer.parseInt(args[2]));
//    }
    public void startClient(String HOST, int PORT){

        Scanner scanner = new Scanner(System.in);

        NioEventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap client = new Bootstrap();
            client.group(group);
            client.channel(NioSocketChannel.class);
            client.handler(new ChannelInitializer<SocketChannel>() {

                public void initChannel(SocketChannel ch){
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new ClientHandler());
                }
            });

            System.out.println("Going to connect with " + HOST + " " + PORT);
            ChannelFuture future = client.connect(HOST, PORT);

            while(scanner.hasNext()){
                String input = scanner.nextLine();
                lamport_counter = lamport_counter + 1;
                String record = input + "," + lamport_counter;

                // Now before executing this statement i.e save in the file, check if another server has entries lesser than current lamport_counter

                Channel channel = null;
                try {
                    channel = future.channel();
                    channel.writeAndFlush(record);
                    channel.flush();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            future.channel().closeFuture().sync();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {

        }

    }
}
