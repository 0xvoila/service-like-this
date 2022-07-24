package org.system.amit.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FileMessageCodec extends ByteToMessageCodec<FileMessage> {

    protected void encode(ChannelHandlerContext ctx, FileMessage msg, ByteBuf out) throws Exception{

        File file = new File(msg.getFileCommand().replace("file:", ""));
        String fileData = Files.readString(file.toPath());
        String message = fileData;
        out.writeBytes(message.getBytes(StandardCharsets.UTF_8));
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
        out.add(in.toString());
    }
}
