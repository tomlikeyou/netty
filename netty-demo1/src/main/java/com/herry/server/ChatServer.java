package com.herry.server;

import com.herry.protocol.MessageCodecSharable;
import com.herry.protocol.ProtocolFrameDecoder;
import com.herry.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {

                            channel.pipeline().addLast(new ProtocolFrameDecoder());
                            channel.pipeline().addLast(LOGGING_HANDLER);
                            channel.pipeline().addLast(MESSAGE_CODEC);
                            //???????????? ????????? ?????????????????? ??? ??????????????????
                            //5????????????????????????channel ???????????????????????????IdleState#READER_IDLE??????
                            channel.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                            //ChannelDuplexHandler ???????????????????????????????????????????????????
                            channel.pipeline().addLast(new ChannelDuplexHandler() {
                                //????????????????????????
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    //????????????????????????
                                    if (event.state() == IdleState.READER_IDLE) {
                                        log.debug("??????5????????????????????????...");
                                        ctx.channel().close();
                                    }
                                }
                            });
                            channel.pipeline().addLast("login", LOGIN_HANDLER);
                            channel.pipeline().addLast(CHAT_HANDLER);
                            channel.pipeline().addLast(GROUP_CREATE_HANDLER);
                            channel.pipeline().addLast(GROUP_JOIN_HANDLER);
                            channel.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                            channel.pipeline().addLast(GROUP_CHAT_HANDLER);
                            channel.pipeline().addLast(GROUP_QUIT_HANDLER);
                            channel.pipeline().addLast(QUIT_HANDLER);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(9000).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
