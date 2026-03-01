package com.gray.web.controller;


import com.gray.web.dto.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/gray")
public class GrayController {
    private static final Logger logger = LoggerFactory.getLogger(GrayController.class);


    protected ThreadPoolExecutor executor = new MyThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000),
            runnable -> new Thread(null, runnable, "async-task", 0L), (runnable, threadPoolExecutor) -> logger.warn("健康检测线程池已满，丢弃任务..."));


    @GetMapping("/runnable")
    public RestResult execute() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("run ... ");
                Runnable r = (Runnable) this;
                System.out.println("xxxxx" + r.getClass().getName());
            }
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/runnable2")
    public RestResult execute2() {
        //lambda 表达式不能拦截进去
        executor.execute(() -> logger.info("run ... "));
        return RestResult.buildSuccess();
    }

    @GetMapping("/callable")
    public RestResult submit() {
        executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                logger.info("call ... ");
                return null;
            }
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/callable2")
    public RestResult submit2() {
        executor.submit((Callable<Boolean>) () -> {
            logger.info("call ... ");
            return null;
        });
        return RestResult.buildSuccess();
    }


}
