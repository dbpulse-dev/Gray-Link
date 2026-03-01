package com.dbcat.gray.server;

import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        EnvUtils.printEnv();
        return "Hello, " + name + " from Provider!";
    }
}