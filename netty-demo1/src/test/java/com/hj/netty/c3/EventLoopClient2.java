package com.hj.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class EventLoopClient2 {
    public static void main(String[] args) throws InterruptedException {
        //222带有future,promise这种类型的都是跟异步方法配套使用,用来处理结果
        //1.启动器
        ChannelFuture channelFuture = new Bootstrap()
                //2.添加EventLoop
                .group(new NioEventLoopGroup())
                //3.选择客户端channel的实现
                .channel(NioSocketChannel.class)
                //4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    //在连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //5.连接到服务器,connect方法是异步非阻塞的，main线程调用之后不会阻塞在这里，继续执行后面代码,
                // 在连接未建立好之前获得一个channel，这个channel是未准备好相关资源的，那么自然也就发送不出去消息，
                //因此必须调用sync阻塞方法，等待连接建立好,那么解决办法有哪些？
                //1.使用sync方法让主线程阻塞等待nio线程建立连接,详细代码看能222.1
                //2.使用回调机制，在nio线程建立连接之后，让nio线程来执行后续逻辑，详细代码看222.2
                .connect(new InetSocketAddress("localhost", 9000));
        //222.1解决方法使用sync方法同步处理结果
        /*channelFuture.sync();//阻塞住当前线程，直到nio线程连接建立
        Channel channel = channelFuture.channel();
        channel.writeAndFlush("hello,world");*/

        //222.2使用回调机制，让nio线程连接建立好之后，nio线程自己来处理后续逻辑
        channelFuture.addListener(new ChannelFutureListener() {
            //nio线程连接建立好之后，nio线程会调用这个方法处理后续逻辑
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("{}", channel);
                channel.writeAndFlush("hello,world");
            }
        });
    }
}
