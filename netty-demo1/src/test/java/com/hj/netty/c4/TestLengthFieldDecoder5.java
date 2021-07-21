package com.hj.netty.c4;

import com.google.common.base.Utf8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 学习netty自带的LengthFieldBasedFrameDecoder 解码器
 */
@Slf4j
public class TestLengthFieldDecoder5 {
    public static void main(String[] args) {
        EmbeddedChannel channel  = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,0,4,1,5),
                new LoggingHandler(LogLevel.DEBUG)
        );
        //四个字节的内容长度,实际内容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        send(buf,"Hello, World");
        send(buf,"Hi!");
        channel.writeInbound(buf);
    }

    public static void send(ByteBuf buf,String content){
        byte[] bytes = content.getBytes();//实际内容
        buf.writeInt(bytes.length);//内容长度
        buf.writeByte(1);
        buf.writeBytes(bytes);
    }

}
