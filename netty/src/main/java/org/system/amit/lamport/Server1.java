package org.system.amit.lamport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Server1 {

    String serverName;
    String HOST;
    int PORT;

    public Server1(String SERVERNAME, String HOST, int PORT){
        this.serverName = SERVERNAME;
        this.HOST = HOST;
        this.PORT = PORT;
    }

//    public static void main(String args[]){
//
//        Server1 server1 = new Server1(args[0],args[1], Integer.parseInt(args[2]));
//        server1.startServer();
//    }
    public void startServer(){

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap server = new ServerBootstrap();
            server.channel(NioServerSocketChannel.class);
            server.group(bossGroup, workerGroup);

            System.out.println("Amit is a good boy");
            System.out.println("Host is " + this.HOST);
            System.out.println("Port is " + this.PORT);

            server.childHandler(new ChannelInitializer<SocketChannel>() {

                public void initChannel(SocketChannel nioServerSocketChannel){
                    ChannelPipeline pipeline = nioServerSocketChannel.pipeline();
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new Server1Handler());
                }
            });

            System.out.println("Host is " + HOST);
            System.out.println("Port is " + PORT);
            ChannelFuture ft = server.bind(HOST,PORT).sync();
            ft.channel().closeFuture().sync();

        }
        catch(Exception e){
            System.out.println("Exception " + e.getMessage());
        }
        finally {
            System.out.println("Calling from finally");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
