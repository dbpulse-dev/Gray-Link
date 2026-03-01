package com.gray.web.controller;


import com.gray.web.dto.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    private static final Logger logger = LoggerFactory.getLogger(ThreadController.class);
    protected ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000),
            runnable -> new Thread(null, runnable, "async-task", 0L), (runnable, threadPoolExecutor) -> logger.warn("健康检测线程池已满，丢弃任务..."));


    protected ThreadPoolExecutor executor2 = new MyThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000),
            runnable -> new Thread(null, runnable, "async-task2", 0L), (runnable, threadPoolExecutor) -> logger.warn("健康检测线程池已满，丢弃任务..."));


    @Autowired
    private MessageService messageService;

    @GetMapping("/runnable")
    public RestResult execute(int type) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                messageService.doSendMessage(type);
            }
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/runnable2")
    public RestResult execute2(int type) {
        //lambda Runnable 无法增强，使用thread-pool 拦截代理封装
        //需增强 jdk thread pool 才有效
        executor.execute(() -> {
            messageService.doSendMessage(type);
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/runnable3")
    public RestResult execute3(int type) {
        //lambda Runnable 无法增强，使用thread-pool 拦截代理封装
        //自定义threadPool
        executor2.execute(() -> {
            messageService.doSendMessage(type);
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/runnable4")
    public RestResult execute4(int type) {
        //lambda Runnable 无法增强，使用thread-pool 拦截代理封装
        new Thread() {
            @Override
            public void run() {
                messageService.doSendMessage(type);
            }
        }.start();
        return RestResult.buildSuccess();
    }

    @GetMapping("/callable")
    public RestResult submit(int type) {
        executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                messageService.doSendMessage(type);
                return null;
            }
        });
        return RestResult.buildSuccess();
    }

    //ServerContextHolder.getData("x-env-")
    @GetMapping("/callable2")
    public RestResult submit2(int type) {
        //lambda Callable 无法增强，使用thread-pool 拦截代理封装
        //需增强 jdk thread pool 才有效
        executor.submit((Callable<Boolean>) () -> {
            messageService.doSendMessage(type);
            return null;
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/callable3")
    public RestResult submit3(int type) {
        //lambda Callable 无法增强，使用thread-pool 拦截代理封装
        executor2.submit((Callable<Boolean>) () -> {
            messageService.doSendMessage(type);
            return null;
        });
        return RestResult.buildSuccess();
    }


    @GetMapping("/async")
    public RestResult async(int type) {
        messageService.asyncSend(type);
        return RestResult.buildSuccess();
    }

}
