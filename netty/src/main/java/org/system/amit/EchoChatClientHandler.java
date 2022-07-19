package org.system.amit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoChatClientHandler extends SimpleChannelInboundHandler<String> {

    public void channelRead0(ChannelHandlerContext ctx, String msg){
        System.out.println(msg);
    }
}
