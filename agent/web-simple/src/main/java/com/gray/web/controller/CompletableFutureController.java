package com.gray.web.controller;

import com.gray.web.util.EnvUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@RestController
@RequestMapping("/complete")
public class CompletableFutureController {

    // 创建自定义线程池
    ExecutorService executor = Executors.newFixedThreadPool(2);
    
    /**
     * 示例1: 创建CompletableFuture并获取结果
     */
    @GetMapping("/test1")
    public  void example1() throws ExecutionException, InterruptedException {
        System.out.println("示例1: 创建和获取结果");
        EnvUtils.printEnv();
        // 使用 supplyAsync 创建有返回值的异步任务,需要对ForkJoinPool
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1执行中... 当前线程: " + Thread.currentThread().getName());
            EnvUtils.printEnv();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "任务1完成";
        });
        
        // 使用 get() 阻塞获取结果
        String result = future1.get();
        System.out.println("任务1结果: " + result + "\n");
    }
    
    /**
     * 示例2: 异步执行任务（无返回值）
     */
    @GetMapping("/test2")
    public   void example2() throws ExecutionException, InterruptedException {
        System.out.println("示例2: 异步执行任务（无返回值）");
        EnvUtils.printEnv();
        // 使用 runAsync 创建无返回值的异步任务
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("任务2执行中... 当前线程: " + Thread.currentThread().getName());
            EnvUtils.printEnv();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("任务2完成");
        });
        
        // 等待任务完成
        future.get();
        System.out.println();
    }
    
    /**
     * 示例3: 链式调用（thenApply, thenAccept, thenRun）
     */
    @GetMapping("/test3")
    public   void example3() throws ExecutionException, InterruptedException {
        System.out.println("示例3: 链式调用");
        
        // 创建第一个任务
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("阶段1: 获取用户ID");
            EnvUtils.printEnv();
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "用户123";
        });
        
        // thenApply: 对结果进行转换
        CompletableFuture<String> future2 = future.thenApply(userId -> {
            System.out.println("阶段2: 根据用户ID获取用户信息");
            EnvUtils.printEnv();
            return "用户信息: " + userId;
        });
        
        // thenAccept: 消费结果，无返回值
        CompletableFuture<Void> future3 = future2.thenAccept(info -> {
            System.out.println("阶段3: 处理用户信息: " + info);
            EnvUtils.printEnv();
        });
        
        // thenRun: 在前一个任务完成后执行，不接收参数也不返回结果
        CompletableFuture<Void> future4 = future3.thenRun(() -> {
            System.out.println("阶段4: 所有处理完成");
            EnvUtils.printEnv();
        });
        
        future4.get();
        System.out.println();
    }
    
    /**
     * 示例4: 组合多个Future（thenCompose, thenCombine）
     */
    @GetMapping("/test4")
    public   void example4() throws ExecutionException, InterruptedException {
        System.out.println("示例4: 组合多个Future");
        
        // 模拟获取用户ID
        CompletableFuture<String> getUserFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("获取用户ID...");
            EnvUtils.printEnv();
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "用户1001";
        });
        
        // 模拟获取用户订单
        CompletableFuture<String> getOrderFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("获取用户订单...");
            EnvUtils.printEnv();
            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "订单A001";
        });
        
        // thenCombine: 组合两个独立的Future，都完成后执行
        CompletableFuture<String> combinedFuture = getUserFuture.thenCombine(getOrderFuture, 
            (userId, orderId) -> {
                EnvUtils.printEnv();
                System.out.println("组合用户和订单信息...");
                return "用户: " + userId + ", 订单: " + orderId;
            });
        
        System.out.println("组合结果: " + combinedFuture.get());
        System.out.println();
    }
    

    
    /**
     * 示例6: 使用自定义线程池
     */
    @GetMapping("/test5")
    public  void example6() throws ExecutionException, InterruptedException {
        System.out.println("示例6: 使用自定义线程池");
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1 - 自定义线程池 - 线程: " + Thread.currentThread().getName());
            EnvUtils.printEnv();
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "结果1";
        }, executor);
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2 - 自定义线程池 - 线程: " + Thread.currentThread().getName());
            EnvUtils.printEnv();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "结果2";
        }, executor);
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2);
        allFutures.get();
        
        System.out.println("任务1结果: " + future1.get());
        System.out.println("任务2结果: " + future2.get());
    }
}