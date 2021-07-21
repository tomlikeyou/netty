package com.hj.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * 学习netty的future 继承自jdk的future
 */
@Slf4j
public class TestNettyFuture5 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("机型计算");
                Thread.sleep(2000);
                return 70;
            }
        });
        //3.主线程通过future获取结果
        log.debug("等待结果");
//        Integer integer = future.get();
//        log.debug("结果是{}", integer);
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("接受结果{}", future.getNow());
            }
        });
    }
}
