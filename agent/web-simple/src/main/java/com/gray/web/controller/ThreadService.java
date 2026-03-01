package com.gray.web.controller;

import com.gray.web.util.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ThreadService {
    private static final Logger logger = LoggerFactory.getLogger(ThreadService.class);

    @Async
    void test(){
        logger.info("thread={}",Thread.currentThread().getName());
        EnvUtils.printEnv();
    }
}
