package com.hj.nio.c1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestByteBuffer {
    public static void main(String[] args) {
        //1.FileChannel
        try {
            FileChannel fileChannel = new FileInputStream("data.txt").getChannel();
            //准备缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true) {
                //从channel 读取数据到buffer
                int len = fileChannel.read(byteBuffer);
                if (len == -1) {
                    break;
                }
                //切换到读模式
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    byte b = byteBuffer.get();
                    System.out.println((char) b);
                }
                //切换到写模式
                byteBuffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
