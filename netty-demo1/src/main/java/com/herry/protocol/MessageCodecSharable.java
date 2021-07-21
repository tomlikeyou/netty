package com.herry.protocol;

import com.herry.config.Config;
import com.herry.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> outList) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        //1. 4字节的魔数
        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 1字节的版本号
        byteBuf.writeByte(1);
        //3. 1字节的序列化方式 jdk 0 ;json 1
        byteBuf.writeByte(Config.getSerializerAlgorithm().ordinal());
        //4. 1字节的指令类型
        byteBuf.writeByte(message.getMessageType());
        //5. 4个字节的请求序号
        byteBuf.writeInt(message.getSequenceId());
        //无意义，对其填充
        byteBuf.writeByte(0xff);
        //6. 长度
        //7.获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serialize(message);
        byteBuf.writeInt(bytes.length);
        //写入内容
        byteBuf.writeBytes(bytes);
        outList.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerAlgorithm = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();
        int contentLength = byteBuf.readInt();
        byte[] bytes = new byte[contentLength];
        byteBuf.readBytes(bytes, 0, contentLength);
        //找到反序列化算法
        Serializer.Algorithm serializer = Serializer.Algorithm.values()[serializerAlgorithm];
        Class<?> messageClass = Message.getMessageClass(messageType);
        Object deserialize = serializer.deserialize(messageClass, bytes);
//        log.debug("{},{},{},{},{},{}", magicNum, version, serializerAlgorithm, messageType, sequenceId, contentLength);
//        log.debug("messag:{}", message);
        list.add(deserialize);
    }
}
