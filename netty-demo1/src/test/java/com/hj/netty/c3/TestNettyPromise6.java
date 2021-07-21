package com.hj.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * 学习netty promise 继承子netty的future
 */
@Slf4j
public class TestNettyPromise6 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.准备eventLoop
        EventLoop eventLoop = new NioEventLoopGroup().next();
        //2.主动创建一个promise
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(() -> {
            //3.任意一个线程执行计算，计算完成后向promise填充结果
            log.debug("开始计算...");
            try {
                int i = 10 / 0;
                Thread.sleep(2000);
                promise.setSuccess(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }

            //
        }).start();
        //接收结果的线程
        log.debug("等待结果...");
        log.debug("结果是：{}", promise.get());
       /* promise.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("结果是：{}", future.getNow());
            }
        });*/
    }
}
