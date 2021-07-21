package com.herry.client;

import com.herry.message.RpcRequestMessage;
import com.herry.protocol.MessageCodecSharable;
import com.herry.protocol.ProtocolFrameDecoder;
import com.herry.server.handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {

        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(RPC_HANDLER);
                        }
                    });
            Channel channel = bootstrap.connect("localhost", 9000).sync().channel();
            ChannelFuture future = channel.writeAndFlush(
                    new RpcRequestMessage(1, "com.herry.server.service.HelloService", "sayHello",
                    String.class, new Class[]{String.class}, new Object[]{"张三"})).addListener(promise -> {
                if (!promise.isSuccess()) {
                    log.error("error..{}", promise.cause());
                }
            });
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.debug("rpc client error ..", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
