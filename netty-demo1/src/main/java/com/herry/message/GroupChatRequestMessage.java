package com.herry.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupChatRequestMessage extends Message {
    private String from;
    private String groupName;
    private String content;

    public GroupChatRequestMessage() {
    }

    public GroupChatRequestMessage(String from, String groupName, String content) {
        this.from = from;
        this.groupName = groupName;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return GroupChatRequestMessage;
    }
}
