package com.herry.server.handler;

import com.herry.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    //连接断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userName = SessionFactory.getSession().getUserName(ctx.channel());
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经断开连接", userName);
    }

    //捕捉到异常时触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经异常断开,异常是{}", ctx.channel(), cause.getMessage());
    }
}
