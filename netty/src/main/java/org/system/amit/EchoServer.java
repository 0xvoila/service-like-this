package org.system.amit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * Hello world!
 *
 */
public class EchoServer
{
    static final int PORT = 8007;

    public static void main( String[] args )
    {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup);
            server.channel(NioServerSocketChannel.class);
            server.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch ) throws  Exception{
                    ChannelPipeline p = ch.pipeline();

                    p.addLast(new StringDecoder());
                    p.addLast(new StringEncoder());
                    p.addLast(new EchoChatServerHandler());
                }
            });

            ChannelFuture ft = server.bind(PORT).sync();

            ft.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
