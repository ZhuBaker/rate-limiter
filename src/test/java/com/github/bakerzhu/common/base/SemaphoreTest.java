package com.github.bakerzhu.common.base;

import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月11日
 * @modifytime:
 */
public class SemaphoreTest {
    Executor executor = Executors.newFixedThreadPool(10);
    @Test
    public void test1() throws Exception {
        Semaphore semaphore = new Semaphore(3);
        for(int i = 0 ; i < 10 ; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        semaphore.acquire();
                        System.out.println("==========");
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        semaphore.release();
                    }
                }
            });
        }

        Thread.sleep(10000L);

    }
}
