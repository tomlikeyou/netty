package com.hj.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBuffer3 {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("hello,world\nI`m zhangsan\nHo".getBytes());
        spilt(buffer);
        buffer.put("w are you?\n".getBytes());
        spilt(buffer);
    }

    private static void spilt(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int len = i + 1 - buffer.position();
                ByteBuffer target = ByteBuffer.allocate(len);
                byte[] bytes = new byte[len];
                buffer.get(bytes);
                target.put(bytes);
            }
        }
        buffer.compact();
    }
}
