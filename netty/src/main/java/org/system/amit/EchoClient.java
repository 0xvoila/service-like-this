package org.system.amit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Scanner;

public class EchoClient {

    static final String host = "127.0.0.1";
    static final int port = 8007;
    static String clientName;
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Scanner scanner = new Scanner(System.in);
            Bootstrap client = new Bootstrap();
            client.group(group);
            client.channel(NioSocketChannel.class);
            client.handler(new ChannelInitializer<SocketChannel>() {

                public void initChannel(SocketChannel ch){
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new EchoChatClientHandler());
                }
            });

            ChannelFuture future = client.connect(host, port);
            while(scanner.hasNext()){
                String input = scanner.nextLine();
                Channel channel = null;
                try {
                    channel = future.sync().channel();
                    channel.writeAndFlush(input);
                    channel.flush();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            future.channel().closeFuture().sync();
        }  finally {
            group.shutdownGracefully();
        }
    }

}
