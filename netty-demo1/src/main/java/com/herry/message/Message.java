package com.herry.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class Message implements Serializable {
    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;
    private int messageType;

    public abstract int getMessageType();

    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    public static final int GroupCreateRequestMessage = 4;
    public static final int GroupCreateResponseMessage = 5;
    public static final int GroupJoinRequestMessage = 6;
    public static final int GroupJoinResponseMessage = 7;
    public static final int GroupQuitRequestMessage = 8;
    public static final int GroupQuitResponseMessage = 9;
    public static final int GroupChatRequestMessage = 10;
    public static final int GroupChatResponseMessage = 11;
    public static final int GroupMembersRequestMessage = 12;
    public static final int GroupMembersResponseMessage = 13;
    public static final int PingMessage = 14;
    public static final int PongMessage = 15;
    public static final int RPC_Message_TYPE_REQUEST = 101;
    public static final int RPC_Message_TYPE_RESPONSE = 102;
    private static final Map<Integer, Class<?>> messageClasses = new ConcurrentHashMap<>();

    static {
        messageClasses.putIfAbsent(LoginRequestMessage, com.herry.message.LoginRequestMessage.class);
        messageClasses.putIfAbsent(LoginResponseMessage, com.herry.message.LoginResponseMessage.class);
        messageClasses.putIfAbsent(ChatRequestMessage, com.herry.message.ChatRequestMessage.class);
        messageClasses.putIfAbsent(ChatResponseMessage, com.herry.message.ChatResponseMessage.class);
        messageClasses.putIfAbsent(GroupCreateRequestMessage, com.herry.message.GroupCreateRequestMessage.class);
        messageClasses.putIfAbsent(GroupCreateResponseMessage, com.herry.message.GroupCreateResponseMessage.class);
        messageClasses.putIfAbsent(GroupJoinRequestMessage, com.herry.message.GroupJoinRequestMessage.class);
        messageClasses.putIfAbsent(GroupJoinResponseMessage, com.herry.message.GroupJoinResponseMessage.class);
        messageClasses.putIfAbsent(GroupQuitRequestMessage, com.herry.message.GroupQuitRequestMessage.class);
        messageClasses.putIfAbsent(GroupQuitResponseMessage, com.herry.message.GroupQuitResponseMessage.class);
        messageClasses.putIfAbsent(GroupChatRequestMessage, com.herry.message.GroupChatRequestMessage.class);
        messageClasses.putIfAbsent(GroupChatResponseMessage, com.herry.message.GroupChatResponseMessage.class);
        messageClasses.putIfAbsent(GroupMembersRequestMessage, com.herry.message.GroupMembersRequestMessage.class);
        messageClasses.putIfAbsent(GroupMembersResponseMessage, com.herry.message.GroupMembersResponseMessage.class);
        messageClasses.putIfAbsent(RPC_Message_TYPE_REQUEST, com.herry.message.RpcRequestMessage.class);
        messageClasses.putIfAbsent(RPC_Message_TYPE_RESPONSE, com.herry.message.RpcResponseMessage.class);
    }
}
