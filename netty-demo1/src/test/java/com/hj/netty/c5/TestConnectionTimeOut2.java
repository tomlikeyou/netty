package com.hj.netty.c5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestConnectionTimeOut2 {
    public static void main(String[] args) {
        //客户端设置参数 使用.option方法
        //服务端给服务端设置参数  使用.option方法
        //服务端给客户端设置参数 使用.childOption方法
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap().group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .handler(new LoggingHandler())
                    .connect("localhost", 9090);
            channelFuture.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("timeout...");
        } finally {
            worker.shutdownGracefully();
        }
    }
}
