package com.herry.server.session;

public class GroupSessionFactory {
    public static final GroupSessionMemoryImpl GROUP_INSTANCE = new GroupSessionMemoryImpl();

    public static GroupSessionMemoryImpl getGroupSession() {
        return GROUP_INSTANCE;
    }
}
