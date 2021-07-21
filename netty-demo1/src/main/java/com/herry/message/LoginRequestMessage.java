package com.herry.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String userName;
    private String password;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
