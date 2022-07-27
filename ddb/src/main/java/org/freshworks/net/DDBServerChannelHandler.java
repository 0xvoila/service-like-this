package org.freshworks.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.freshworks.Queue.GlobalUsage;


public class DDBServerChannelHandler extends SimpleChannelInboundHandler<String> {

    public void channelRegistered(ChannelHandlerContext ctx){

    }

    public void channelUnregistered(ChannelHandlerContext ctx){

    }

    public void channelActive(ChannelHandlerContext ctx){
        System.out.println("Channel is active" + ctx.channel().id());
    }

    public void channelInactive(ChannelHandlerContext ctx){
        System.out.println("Channel is inactive" + ctx.channel().id());
    }

    public void channelRead0(ChannelHandlerContext ctx, String msg){

        System.out.println("Reading message is " + msg);
        GlobalUsage.writeQueue.add(msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx){

    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("Exception occured with cause " + cause.getMessage());
    }
}
