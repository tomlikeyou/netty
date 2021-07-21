package com.herry.server.handler;

import com.herry.message.GroupCreateRequestMessage;
import com.herry.message.GroupCreateResponseMessage;
import com.herry.server.session.Group;
import com.herry.server.session.GroupSessionFactory;
import com.herry.server.session.GroupSessionMemoryImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        //群管理器
        GroupSessionMemoryImpl groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (Objects.isNull(group)) {
            //发送成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
            //发送拉群消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            membersChannel.stream().forEach(channel -> channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入" + groupName + "群中")));
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已经存在"));
        }
    }
}
