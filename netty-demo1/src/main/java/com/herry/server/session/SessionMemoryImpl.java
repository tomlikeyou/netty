package com.herry.server.session;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryImpl implements Session {
    private final Map<String, Channel> userNameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelUserNameMap = new ConcurrentHashMap<>();
    private final Map<Channel, Map<String, Object>> channelAttributesMap = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel channel, String userName) {
        userNameChannelMap.put(userName, channel);
        channelUserNameMap.put(channel, userName);
        channelAttributesMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        String userName = channelUserNameMap.get(channel);
        userNameChannelMap.remove(userName);
        channelAttributesMap.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributesMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        channelAttributesMap.get(channel).put(name, value);
    }

    @Override
    public Channel getChannel(String userName) {
        return userNameChannelMap.get(userName);
    }

    @Override
    public String getUserName(Channel channel) {
        return channelUserNameMap.get(channel);
    }


}
