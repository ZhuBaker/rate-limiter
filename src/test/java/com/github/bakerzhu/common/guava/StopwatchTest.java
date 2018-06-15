package com.github.bakerzhu.common.guava;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:Stopwatch源码解析：https://my.oschina.net/LucasZhu/blog/1809850
 * @time: 2018年06月13日
 * @modifytime:
 */
public class StopwatchTest {

    public static void main(String[] args) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Thread.sleep(1231L);
        stopwatch.stop();
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println(elapsed);
        System.out.println(("time: " + stopwatch)); // formatted string like "12.3 ms"

        Thread.sleep(2000L);
        stopwatch.start();
        Thread.sleep(1232L);
        elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println(elapsed);
        System.out.println(("time: " + stopwatch)); // formatted string like "12.3 ms"


        stopwatch.reset().start();
        Thread.sleep(1232L);
        elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println(elapsed);
        System.out.println(("time: " + stopwatch)); // formatted string like "12.3 ms"
    }




}
