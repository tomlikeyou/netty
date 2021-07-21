package com.hj.nio.c1;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestByteBufferString {

    public static void main(String[] args) {
        //1.字符串转buffer,该种方式用完之后还是处于写模式；用2 或3 则直接是处于读模式
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("hello".getBytes());
        System.out.println(buffer);

        //2.Charset
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");
        System.out.println(buffer1);
        //3.wrap
        ByteBuffer buffer2 = ByteBuffer.wrap("hello".getBytes());
        System.out.println(buffer2);
        //转为字符串
        String s1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(s1);
        buffer.flip();
        String s2 = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(s2);

        //测试分散读取
        try (FileChannel channel = new RandomAccessFile("data.txt", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(3);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            System.out.println(b1);
            System.out.println(b2);
            System.out.println(b3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //测试集中写
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("tom");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");
        try {
            FileChannel channel = new RandomAccessFile("words.txt", "rw").getChannel();
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
