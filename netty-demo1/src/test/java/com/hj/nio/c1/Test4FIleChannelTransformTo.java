package com.hj.nio.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 *
 */
public class Test4FIleChannelTransformTo {
    public static void main(String[] args) {
        try {
            FileChannel from = new FileInputStream("data.txt").getChannel();
            FileChannel to = new FileOutputStream("to.txt").getChannel();
            //效率高.底层会使用操作系统的零拷贝进行优化
            long size = from.size();
            for (long left = size; left > 0; ) {
                left = from.transferTo(size - left, left, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
