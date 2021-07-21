package com.hj.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import lombok.extern.slf4j.Slf4j;

import static com.hj.netty.c3.TestByteBuf8.log;

/**
 * 学习将多个小的buf逻辑组合到一个buf里面,也是属于零拷贝;好处就是避免了内存的复制
 */
@Slf4j
public class TestCompositeByteBuf2 {
    public static void main(String[] args) {
        ByteBuf bu1 = ByteBufAllocator.DEFAULT.buffer();
        bu1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        CompositeByteBuf compositeBuffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeBuffer.addComponents(true, bu1, buf2);
        log(compositeBuffer);
    }
}
