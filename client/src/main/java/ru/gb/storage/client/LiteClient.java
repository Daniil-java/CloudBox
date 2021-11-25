package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.gb.storage.commons.handler.JsonDecoder;
import ru.gb.storage.commons.handler.JsonEncoder;
import ru.gb.storage.commons.message.DownloadFileRequestMessage;
import ru.gb.storage.commons.message.FileMessage;
import ru.gb.storage.commons.message.Message;
import ru.gb.storage.commons.message.TextMessage;

import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Scanner;

public class LiteClient {
    private static final String EXIT_WORD = "/exit";

    public static void main(String[] args) throws InterruptedException {
        new LiteClient().run();

    }

    public void run() throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(512, 0, 2, 0, 2),
                                    new LengthFieldPrepender(2),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
                                            if(msg instanceof FileMessage) {
                                                var message = (FileMessage) msg;
                                                try (RandomAccessFile randomAccessFile = new RandomAccessFile("1", "rw")) {
                                                    randomAccessFile.write(message.getContent());
                                                    System.out.println("Файл");
                                                }
                                                ctx.close();
                                            }
                                        }
                                    }
                            );
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = bootstrap.connect("localhost", 9000).sync().channel();

//            final DownloadFileRequestMessage message = new DownloadFileRequestMessage();
//            message.setPath("C:\\Java\\network-storage-template-master\\commons\\src\\main\\java\\ru\\gb\\storage\\commons\\message\\test.json");
//            channel.writeAndFlush(message);

            TextMessage textMessage = new TextMessage();
            textMessage.setText("/download");
            channel.writeAndFlush(textMessage);
        } finally {
            worker.shutdownGracefully();
        }
    }
}