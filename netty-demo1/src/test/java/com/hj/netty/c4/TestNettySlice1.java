package com.hj.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.hj.netty.c3.TestByteBuf8.log;

/**
 * 学习netty slice方法 测试零拷贝
 */
public class TestNettySlice1 {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(buf);
        //在切片过程中没有发生过内存复制
        ByteBuf buf1 = buf.slice(0, 5);
        ByteBuf buf2 = buf.slice(5, 5);
        log(buf1);
        log(buf2);
        System.out.println("===============");
        buf1.setByte(0,'b');
        log(buf1);
        log(buf);
    }
}
