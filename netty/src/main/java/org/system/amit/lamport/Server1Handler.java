package org.system.amit.lamport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Server1Handler extends SimpleChannelInboundHandler<String> {

    public void channelRead0(ChannelHandlerContext ctx, String msg){
        System.out.println(msg);
    }

    public void channelActive(ChannelHandlerContext ctx){

        System.out.println("Congrats EchoClient joined ");
    }
}
