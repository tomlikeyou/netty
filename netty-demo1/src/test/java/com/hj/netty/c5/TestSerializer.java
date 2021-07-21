package com.hj.netty.c5;

import com.herry.config.Config;
import com.herry.message.LoginRequestMessage;
import com.herry.message.Message;
import com.herry.protocol.MessageCodecSharable;
import com.herry.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);
        LoginRequestMessage msg = new LoginRequestMessage("zhangsan", "123");
//        channel.writeOutbound(msg);
        ByteBuf buf = messageToByteBuf(msg);
        channel.writeInbound(buf);
    }

    public static ByteBuf messageToByteBuf(Message msg) {
        int ordinal = Config.getSerializerAlgorithm().ordinal();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        buf.writeByte(1);
        buf.writeByte(ordinal);
        buf.writeByte(msg.getMessageType());
        buf.writeInt(msg.getSequenceId());
        buf.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[ordinal].serialize(msg);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }
}
