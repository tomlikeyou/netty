package com.herry.server.service;

public abstract class UserServiceFactory {
    public static final UserServiceMemoryImpl USER_INSTANCE = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return USER_INSTANCE;
    }
}
