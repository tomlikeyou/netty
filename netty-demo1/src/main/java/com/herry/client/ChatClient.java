package com.herry.client;

import com.herry.message.*;
import com.herry.protocol.MessageCodecSharable;
import com.herry.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
            MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
            CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
            AtomicBoolean LOGIN = new AtomicBoolean(false);
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ProtocolFrameDecoder());
                            channel.pipeline().addLast(LOGGING_HANDLER);
                            channel.pipeline().addLast(MESSAGE_CODEC);
                            //用来判断 是不是 读空闲时间长 或 写空闲时间长
                            //3秒内如果没有向服务端数据，会触发一个IdleState#WRITER_IDLE事件
                            channel.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                            //ChannelDuplexHandler 可以同时作为入站处理器跟出站处理器
                            channel.pipeline().addLast(new ChannelDuplexHandler() {
                                //用来触发特殊事件
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    //触发了写空闲事件
                                    if (event.state() == IdleState.WRITER_IDLE) {
                                        channel.writeAndFlush(new PingMessage());
                                    }
                                }
                            });
                            channel.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                                //建立连接
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //接收用户的输入 发送消息
                                    new Thread(() -> {
                                        Scanner scanner = new Scanner(System.in);
                                        System.out.println("请输入用户名:");
                                        String userName = scanner.nextLine();
                                        System.out.println("请输入密码");
                                        String pwd = scanner.nextLine();
                                        LoginRequestMessage loginRequestMessage = new LoginRequestMessage(userName, pwd);
                                        //发送消息
                                        ctx.writeAndFlush(loginRequestMessage);
                                        System.out.println("等待后续操作.....");
                                        try {
                                            WAIT_FOR_LOGIN.await();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        //登录失败
                                        if (!LOGIN.get()) {
                                            ctx.channel().close();
                                            return;
                                        }
                                        while (true) {
                                            System.out.println("-------------------------");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gsend [group name] [content]");
                                            System.out.println("gcreate [group name] [m1,m2,m3..]");
                                            System.out.println("gmembers [group name]");
                                            System.out.println("gjoin [group name]");
                                            System.out.println("gquit [group name]");
                                            System.out.println("quit");
                                            System.out.println("--------------------------");
                                            String command = scanner.nextLine();
                                            String[] s = command.split(" ");
                                            switch (s[0]) {
                                                case "send":
                                                    ChatRequestMessage chatRequestMessage = new ChatRequestMessage(userName, s[1], s[2]);
                                                    ctx.writeAndFlush(chatRequestMessage);
                                                    break;
                                                case "gsend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(userName, s[1], s[2]));
                                                    break;
                                                case "gcreate":
                                                    HashSet<String> members = new HashSet<>(Arrays.asList(s[2].split(",")));
                                                    members.add(userName);
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], members));
                                                    break;
                                                case "gmembers":
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                                    break;
                                                case "gjoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(userName, s[1]));
                                                    break;
                                                case "gquit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(userName, s[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    break;
                                            }
                                        }
                                    }, "system in").start();
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelInactive(ctx);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    super.exceptionCaught(ctx, cause);
                                }

                                //接收消息
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("msg::{}", msg);
                                    if (msg instanceof LoginResponseMessage) {
                                        LoginResponseMessage responseMessage = (LoginResponseMessage) msg;
                                        if (responseMessage.isSuccess()) {
                                            //登录成功设置为true
                                            LOGIN.set(true);
                                        }
                                    }
                                    //不管登录是否成功都唤醒client 执行后续操作
                                    WAIT_FOR_LOGIN.countDown();
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9000).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error..", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
