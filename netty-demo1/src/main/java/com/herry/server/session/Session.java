package com.herry.server.session;

import io.netty.channel.Channel;

/**
 * 会话管理接口
 */
public interface Session {

    /**
     * 绑定会话
     *
     * @param channel  那个channel 要绑定会话
     * @param userName 会话绑定用户
     */
    void bind(Channel channel, String userName);

    /**
     * 解绑会话
     *
     * @param channel
     */
    void unbind(Channel channel);


    Object getAttribute(Channel channel,String name);

    void setAttribute(Channel channel,String name,Object value);

    /**
     * 根据用户名获取channel
     * @param userName
     * @return
     */
    Channel getChannel(String userName);

    /**
     * 根据channel获取userName
     * @param channel
     * @return
     */
    String getUserName(Channel channel);
}
