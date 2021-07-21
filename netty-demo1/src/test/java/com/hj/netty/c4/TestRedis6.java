package com.hj.netty.c4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

/**
 * 结合netty的解析部分学习redis协议解析
 */
public class TestRedis6 {
    public static void main(String[] args) {
        final byte[] LINE = {13, 10};
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            //连接建立后回调该事件
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                //给redis发送命令 set name zhangsan
                                ByteBuf buf = ctx.alloc().buffer();
                                buf.writeBytes("*3".getBytes());
                                buf.writeBytes(LINE);
                                buf.writeBytes("$3".getBytes());
                                buf.writeBytes(LINE);
                                buf.writeBytes("set".getBytes());
                                buf.writeBytes(LINE);
                                buf.writeBytes("$4".getBytes());
                                buf.writeBytes(LINE);
                                buf.writeBytes("name".getBytes());
                                buf.writeBytes(LINE);
                                buf.writeBytes("$8".getBytes());
                                buf.writeBytes(LINE);
                                buf.writeBytes("zhangsan".getBytes());
                                buf.writeBytes(LINE);
                                ctx.writeAndFlush(buf);
                            }
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //收到消息后打印信息
                                ByteBuf buf= (ByteBuf) msg;
                                System.out.println(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9000).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
