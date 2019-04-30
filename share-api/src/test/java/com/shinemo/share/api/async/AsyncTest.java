package com.shinemo.share.api.async;


import com.shinemo.share.api.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AsyncTest {

    private final Executor executor = Executors.newFixedThreadPool(100, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    private final Executor executorLambda = testFunction();

    @Test
    public void testParallel() {
        long startTime = System.currentTimeMillis();
        IntStream.rangeClosed(1, 100)
                .boxed()
                .parallel()
                .forEach(this::sleep);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime));
    }

    @Test
    public void testLessAsync() {
        long startTime = System.currentTimeMillis();
        CompletableFuture[] array = IntStream.rangeClosed(1, 100)
                .boxed()
                .map(i -> CompletableFuture.supplyAsync(() -> sleep(i)))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(array).join();
        long endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime));
    }

    @Test
    public void testAsync() {
        long startTime = System.currentTimeMillis();
        List<CompletableFuture<Integer>> collect = IntStream.rangeClosed(1, 100)
                .boxed()
                .map(i -> CompletableFuture.supplyAsync(() -> sleep(i), executor))
                .collect(toList());
        List<Integer> collect1 = collect.stream().map(CompletableFuture::join).collect(toList());
        long endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime));

    }

    private Integer sleep(Integer i) {
        try {
            Thread.sleep(1000L);
            System.out.println("我是第" + i + "个执行的");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }

    private Executor createExecutor() {
        return Executors.newFixedThreadPool(10, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }


    private Executor testFunction() {
        return createFunction((Runnable r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

    private Executor createFunction(Function<Runnable, Thread> f) {
        return Executors.newFixedThreadPool(20, f::apply);
    }


}
