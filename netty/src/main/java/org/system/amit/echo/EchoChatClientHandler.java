package org.system.amit.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoChatClientHandler extends SimpleChannelInboundHandler<String> {

    ChannelHandlerContext ctx;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.channelActive(ctx);
    }

    public void channelRead0(ChannelHandlerContext ctx, String msg){
        System.out.println(msg);
    }

    public void sendMessage(String msg){
        this.ctx.writeAndFlush(msg, ctx.newPromise());
    }
}
