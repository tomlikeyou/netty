package com.herry.server.service;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        int i = 10 / 0;
        return "你好，" + name;
    }
}
