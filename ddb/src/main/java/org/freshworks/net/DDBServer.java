package org.freshworks.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class DDBServer {

    final static String HOST = "127.0.0.1";
    final static int PORT = 7000;

    public void DDBServerStart(){

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup);
            server.channel(NioServerSocketChannel.class);

            server.childHandler(new ChannelInitializer<NioServerSocketChannel>() {

                public  void initChannel(NioServerSocketChannel socketChannel) throws Exception{
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new DDBServerChannelHandler());
                }
            });

            ChannelFuture ft =  server.bind(HOST, PORT).sync();

            ft.channel().closeFuture().sync();
        }
        catch(Exception e){
            System.out.println("Exception occured in DDB Server " + e.getMessage());
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
