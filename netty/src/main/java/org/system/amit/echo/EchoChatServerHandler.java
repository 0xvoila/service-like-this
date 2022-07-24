package org.system.amit.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoChatServerHandler extends SimpleChannelInboundHandler<String> {

    public void channelActive(ChannelHandlerContext ctx){

        System.out.println("Congrats EchoClient joined ");
    }

    public void channelRead0(ChannelHandlerContext ctx, String msg){
        System.out.println("Message received is " + msg);
        ctx.channel().writeAndFlush(msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("Some exception has occured");
        ctx.close();
    }
}
