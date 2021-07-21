package com.hj.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class Server {
    private static void spilt(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int len = i + 1 - buffer.position();
                ByteBuffer target = ByteBuffer.allocate(len);
                byte[] bytes = new byte[len];
                buffer.get(bytes);
                target.put(bytes);
                target.flip();
                System.out.println(Charset.defaultCharset().decode(target));
            }
        }
        buffer.compact();
    }

    public static void main(String[] args) throws IOException {
        //1.创建一个selector
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞模式

        //2.建立selector跟channel的联系-》注册
        //SelectionKey 就是将来事件发生后，通过它能知道事件跟哪个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        //只关注连接事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        System.out.println("register key:" + sscKey);

        ssc.bind(new InetSocketAddress(9999));
        while (true) {
            //3. 没有事件发生，线程阻塞，有事件发生，线程恢复运行,事件必须处理
            selector.select();
            //4.处理事件，包含了所有的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                System.out.println("key:" + key);
                //5.区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    //将buffer作为附件关联到selectionKey
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    System.out.println("socketchannel" + sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel sc = (SocketChannel) key.channel();
                        //获得selectionKey的附件 buffer
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        //如果是正常断开，read的方法返回值为-1
                        int len = sc.read(buffer);
                        if (len == -1) {
                            key.cancel();
                        } else {
                            spilt(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //因为客户端断开了，需要将key取消,从selector的注册keys真正删除掉
                        key.cancel();
                    }
                }
            }
        }
    }
}
