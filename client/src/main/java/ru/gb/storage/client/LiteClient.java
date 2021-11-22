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
                                    new StringEncoder(),
                                    new StringDecoder(),
                                    new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
                                            System.out.println("Incoming message: " + msg);
                                        }
                                    }
                            );
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = bootstrap.connect("localhost", 9000).sync().channel();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Введите ваше сообщение");
                if (scanner.hasNextLine()) {
                    String msg = scanner.nextLine();
                    if (msg.equals(EXIT_WORD)) {
                        scanner.close();
                        channel.close();
                        break;
                    }
                    channel.writeAndFlush(msg);
                }
            }
        } finally {
            worker.shutdownGracefully();
        }
    }
}
