package com.herry.server.handler;

import com.herry.message.LoginRequestMessage;
import com.herry.message.LoginResponseMessage;
import com.herry.server.service.UserServiceFactory;
import com.herry.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String userName = msg.getUserName();
        String pwd = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(userName, pwd);
        LoginResponseMessage responseMessage;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), userName);
            responseMessage = new LoginResponseMessage(true, "登陆成功");
        } else {
            responseMessage = new LoginResponseMessage(false, "用户名或密码不正确");
        }
        ctx.writeAndFlush(responseMessage);
    }
}
