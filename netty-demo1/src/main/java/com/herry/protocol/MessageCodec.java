package com.herry.protocol;

import com.herry.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    /**
     * 编码
     *
     * @param ctx
     * @param message
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf byteBuf) throws Exception {
        //1. 4字节的魔数
        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 1字节的版本号
        byteBuf.writeByte(1);
        //3. 1字节的序列化方式 jdk 0 ;json 1
        byteBuf.writeByte(0);
        //4. 1字节的指令类型
        byteBuf.writeByte(message.getMessageType());
        //5. 4个字节的请求序号
        byteBuf.writeInt(message.getSequenceId());
        //无意义，对其填充
        byteBuf.writeByte(0xff);
        //6. 长度
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] bytes = bos.toByteArray();
        //7.获取内容的字节数组
        byteBuf.writeInt(bytes.length);
        //写入内容
        byteBuf.writeBytes(bytes);
    }

    /**
     * 解码
     *
     * @param ctx
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();
        int contentLength = byteBuf.readInt();
        byte[] bytes = new byte[contentLength];
        byteBuf.readBytes(bytes, 0, contentLength);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        System.out.println(magicNum);
        System.out.println(version);
        System.out.println(serializerType);
        System.out.println(messageType);
        System.out.println(sequenceId);
        System.out.println(contentLength);
        System.out.println(message);
        list.add(message);
    }
}
