package com.github.bakerzhu.common.guava;

import com.google.common.util.concurrent.RateLimiter;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月13日
 * @modifytime:
 */
public class RateLimiterTest {

    public static void main(String[] args) {

        RateLimiter limiter = RateLimiter.create(2);
        System.out.println(LocalDateTime.now() + " : call execute.. " );  //2018-06-13T21:00:53.879 : call execute..
        limiter.acquire(3);
        System.out.println(LocalDateTime.now() + " : call execute.. " );  //2018-06-13T21:00:53.885 : call execute..
        limiter.acquire(3);
        System.out.println(LocalDateTime.now() + " : call execute.. " );  //2018-06-13T21:00:55.298 : call execute..
        limiter.acquire(3);
        System.out.println(LocalDateTime.now() + " : call execute.. " );  //2018-06-13T21:00:56.797 : call execute..



    }

}
