package ru.gb.storage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import ru.gb.storage.commons.message.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
        if (msg instanceof DownloadFileRequestMessage) {
            var message = (DownloadFileRequestMessage) msg;
            System.out.println("Файл получен сервером");
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(message.getPath(), "r")) {
                final FileMessage fileMessage = new FileMessage();
                byte[] content = new byte[(int) randomAccessFile.length()];
                randomAccessFile.read(content);
                fileMessage.setContent(content);
                ctx.writeAndFlush(fileMessage);
            }
        }
        if (msg instanceof TextMessage  && (((TextMessage) msg).getText().equals("/download"))) {
            DownloadFileRequestMessage downloadFileRequestMessage = new DownloadFileRequestMessage();
            downloadFileRequestMessage.setPath("C:\\Java\\network-storage-template-master\\commons\\src\\main\\java\\ru\\gb\\storage\\commons\\message\\test.json");;
            try(RandomAccessFile randomAccessFile = new RandomAccessFile(downloadFileRequestMessage.getPath(), "r")) {
                final FileMessage fileMessage = new FileMessage();
                byte[] content = new byte[(int) randomAccessFile.length()];
                randomAccessFile.read(content);
                fileMessage.setContent(content);
                ctx.writeAndFlush(fileMessage);
                System.out.println("Отправка Файла");
            }
        }
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("incoming text message: " + message.getText());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof DateMessage) {
            DateMessage message = (DateMessage) msg;
            System.out.println("incoming date message: " + message.getDate());
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        System.out.println("Catch cause " + cause.getMessage());
        ctx.close();
    }
}
