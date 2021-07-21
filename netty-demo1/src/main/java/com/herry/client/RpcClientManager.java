package com.herry.client;

import com.herry.message.RpcRequestMessage;
import com.herry.protocol.MessageCodecSharable;
import com.herry.protocol.ProtocolFrameDecoder;
import com.herry.protocol.SequenceIdGenerator;
import com.herry.server.handler.RpcResponseMessageHandler;
import com.herry.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcClientManager {
    private RpcClientManager() {
    }

    private static Channel channel = null;
    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        HelloService helloService = getProxyService(HelloService.class);
        System.out.println(helloService.sayHello("zhangsan"));;
        System.out.println(helloService.sayHello("lisi"));;
        System.out.println(helloService.sayHello("wangwu"));;
    }

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(classLoader, interfaces, ((proxy, method, args) -> {
                    //1. 将方法转换成 消息对象
                    int sequenceId = SequenceIdGenerator.nextId();
                    RpcRequestMessage msg = new RpcRequestMessage(
                            sequenceId, serviceClass.getName(), method.getName(),
                            method.getReturnType(), method.getParameterTypes(), args);
                    //2.将消息对象发送出去
                    getChannel().writeAndFlush(msg);
                    //3.准备一个空的promise对象，来接收结果               指定promise对象 异步接收结果的线程
                    DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
                    //暂时结果返回null
                    RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);

                    //4.主线程等待  promise结果
                    promise.await();
                    if (promise.isSuccess()) {
                        //调用正常
                        return promise.getNow();
                    } else {
                        //调用失败
                        throw new RuntimeException(promise.cause());
                    }
                })
        );
        return (T) o;
    }


    /**
     * 获得一个唯一的channel
     */
    private static void initChannel() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
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
        try {
            channel = bootstrap.connect("localhost", 9000).sync().channel();
            channel.closeFuture().addListener(future -> {
                worker.shutdownGracefully();
            });
        } catch (Exception e) {
            log.debug("rpc client error ..", e);
        }
    }
}
