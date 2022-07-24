package org.system.amit.lamport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileWriter;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

        public void channelRead0(ChannelHandlerContext ctx, String msg){

            System.out.println(msg);
        }
}
