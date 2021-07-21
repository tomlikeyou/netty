package com.herry.server.session;

public class SessionFactory {
    public static final SessionMemoryImpl SESSION_INSTANCE = new SessionMemoryImpl();

    public static Session getSession() {
        return SESSION_INSTANCE;
    }
}
