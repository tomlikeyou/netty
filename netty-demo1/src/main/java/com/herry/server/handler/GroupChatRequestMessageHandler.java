package com.herry.server.handler;

import com.herry.message.GroupChatRequestMessage;
import com.herry.message.GroupChatResponseMessage;
import com.herry.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String content = msg.getContent();
        GroupSessionFactory.getGroupSession().getMembersChannel(groupName).stream().forEach(
                channel -> {
                    channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent()));
                }
        );
    }
}
