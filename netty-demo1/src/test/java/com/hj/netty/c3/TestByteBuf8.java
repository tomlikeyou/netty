package com.hj.netty.c3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * 学习netty的ByteBuf
 */
public class TestByteBuf8 {
    public static void main(String[] args) {
        //netty4.1版本之前，是默认不使用池化buffer的，4.1之后默认开启，通过设置环境变量可以改变是否池化
        //环境变量设置 -Dio.netty.allocator.type=unpooled/pooled
        //byteBuf 有四个属性 读指针，写指针，capacity容量，最大容量
        //默认这种方式创建的是直接内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        //也是创建了直接内存
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer();
        //创建的是一个堆内存
        ByteBuf buffer1 = ByteBufAllocator.DEFAULT.heapBuffer();
        log(buffer);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append("a");
        }
        buffer.writeBytes(sb.toString().getBytes());
        log(buffer);
    }

    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index :").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
