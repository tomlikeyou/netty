package com.herry.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatResponseMessage extends AbstractResponseMessage{
    private String from;
    private String content;

    public ChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }

    public ChatResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return ChatResponseMessage;
    }
}
