package com.herry.server.handler;

import com.herry.message.RpcRequestMessage;
import com.herry.message.RpcResponseMessage;
import com.herry.server.service.HelloService;
import com.herry.server.service.ServiceFactory;
import com.herry.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        responseMessage.setSequenceId(msg.getSequenceId());
        try {
            HelloService service = (HelloService) ServiceFactory.getService(Class.forName(msg.getInterfaceName()));
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object value = method.invoke(service, msg.getParameterValues());
            responseMessage.setReturnValue(value);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.setExceptionValue(new Exception("远程调用出错" + e.getCause().getMessage()));
        }
        ctx.writeAndFlush(responseMessage);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {
        RpcRequestMessage msg = new RpcRequestMessage(1, "com.herry.server.service.HelloService", "sayHello",
                String.class, new Class[]{String.class}, new Object[]{"张三"});
    }
}
