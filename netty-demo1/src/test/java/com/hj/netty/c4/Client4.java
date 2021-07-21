package com.hj.netty.c4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Random;
/**
 * 学习netty的LineBasedFrameDecoder 解码器 服务端对 \n进行解析
 */
public class Client4 {
    public static void main(String[] args) {
        send();
    }

    public static void send() {
        try {
            NioEventLoopGroup worker = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                //channel建立成功后触发该事件方法
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    char c = '0';
                                    Random r = new Random();
                                    ByteBuf buf = ctx.alloc().buffer();
                                    for (int i = 0; i < 10; i++) {
                                        String s = makeString(c, r.nextInt(256) + 1);
                                        c++;
                                        buf.writeBytes(s.getBytes());
                                    }
                                    ctx.writeAndFlush(buf);
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9000).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String makeString(char c, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(c);
        }
        sb.append("\n");
        return sb.toString();
    }
}
