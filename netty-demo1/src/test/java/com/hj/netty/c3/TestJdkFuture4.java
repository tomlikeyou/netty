package com.hj.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestJdkFuture4 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.线程池
        ExecutorService service = Executors.newFixedThreadPool(2);
        //2.提交任务
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算任务");
                Thread.sleep(2000);
                return 50;
            }
        });
        //3.主线程通过future获取结果
        log.debug("等待结果");
        Integer integer = future.get();
        log.debug("结果是{}", integer);
    }
}
