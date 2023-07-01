package com.imooc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 库存接口类
 */
@RestController
public class StockController {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 使用SETNX指令来实现一个简易的分布式锁(for循环版本)
     * @return
     */
    @GetMapping("/stock/reduce")
    public String reduce() {
        System.out.println("开始扣减库存啦！");

        // 为了防止误删，设置uuid
        String uuid = UUID.randomUUID().toString();

        // 使用setnx指令加锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 5, TimeUnit.SECONDS);
        for ( ;!lock; ) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 5, TimeUnit.SECONDS);
        }

        int stock = 0;

        try {
            // 从redis中获取库存
            stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));

            if (stock > 0) {
                // 库存充足的情况
                stock--;
                redisTemplate.opsForValue().set("stock", stock + "");
            } else {
                // 库存不充足的情况
                throw new RuntimeException("库存不充足！");
            }

            return "剩余库存为：" + stock;
        } catch (Exception e) {

        } finally {
            if (redisTemplate.opsForValue().get("lock").equals(uuid)) {
                // 解锁
                redisTemplate.delete("lock");
            }
        }

        return "剩余库存为：" + stock;
    }

    /**
     * 使用SETNX指令来实现一个简易的分布式锁(递归版本)
     * @return
     */
//    @GetMapping("/stock/reduce")
//    public String reduce() {
//        System.out.println("开始扣减库存啦！");
//
//        // 使用setnx指令加锁
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1");
//        if (!lock) {
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return this.reduce();
//        }
//
//        int stock = 0;
//
//        try {
//            // 从redis中获取库存
//            stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
//
//            if (stock > 0) {
//                // 库存充足的情况
//                stock--;
//                redisTemplate.opsForValue().set("stock", stock + "");
//            } else {
//                // 库存不充足的情况
//                throw new RuntimeException("库存不充足！");
//            }
//
//            return "剩余库存为：" + stock;
//        } catch (Exception e) {
//
//        } finally {
//            // 解锁
//            redisTemplate.delete("lock");
//        }
//
//        return "剩余库存为：" + stock;
//    }

    /**
     * 减少库存的接口
     * @return
     */
//    @GetMapping("/stock/reduce")
//    public synchronized String reduce() {
//        System.out.println("开始扣减库存啦！");
//        // 从redis中获取库存
//        int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
//
//        if (stock > 0) {
//            // 库存充足的情况
//            stock--;
//            redisTemplate.opsForValue().set("stock", stock + "");
//        } else {
//            // 库存不充足的情况
//            throw new RuntimeException("库存不充足！");
//        }
//
//        return "剩余库存为：" + stock;
//    }
//    ReentrantLock reentrantLock = new ReentrantLock();

//    /**
//     * 减少库存的接口
//     * @return
//     */
//    @GetMapping("/stock/reduce")
//    public String reduce() {
//        System.out.println("开始扣减库存啦！");
//
//        try {
//            reentrantLock.lock();
//
//            // 从redis中获取库存
//            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
//
//            if (stock > 0) {
//                // 库存充足的情况
//                stock--;
//                redisTemplate.opsForValue().set("stock", stock + "");
//            } else {
//                // 库存不充足的情况
//                throw new RuntimeException("库存不充足！");
//            }
//
//            return "剩余库存为：" + stock;
//        } catch (Exception ex) {
//
//        } finally {
//            reentrantLock.unlock();
//        }
//
//        return "ok";
//    }



}
