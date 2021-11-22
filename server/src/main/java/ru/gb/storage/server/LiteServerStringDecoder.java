package ru.gb.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class LiteServerStringDecoder extends MessageToMessageDecoder<byte[]> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, byte[] msg, List<Object> out) throws Exception {
        out.add(new String(msg));
    }
}
