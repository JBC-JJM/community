package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    //logger可以带上当前线程的信息
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //线程睡眠
    private void sleep(long m) {//封装一下，懒得报异常
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //线程任务
    private Runnable task(String output) {//封装一下，懒
        return new Runnable() {
            @Override
            public void run() {
                logger.debug(output);
            }
        };
    }


    // 1、 JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Test
    public void testExecutorService() {
        Runnable task = task("Hello ExecutorService");

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);//线程池中获取线程执行task任务
        }
        sleep(10000);//10秒
    }


    // 2、JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    @Test
    public void testScheduledExecutorService() {
        Runnable task = task("Hello ScheduledExecutorService");
        //任务、延时执行、时间间隔、时间单位（毫秒）
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }

    // 3、spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = task("Hello ScheduledExecutorService");

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);//线程池中获取线程执行task任务
        }
        sleep(10000);//10秒
    }

    // 4、 Spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = task("Hello ThreadPoolTaskScheduler");
        Date startTime = new Date(System.currentTimeMillis() + 10000);
        //任务、开始时间、间隔、默认毫秒位单位
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);

        sleep(30000);
    }

    @Autowired
    private AlphaService alphaService;

    //使用注解创建任务：
    //异步线程自动创建，只要调用即可
    //定时器的线程自动创建自动开始
    // 5.Spring普通线程池(简化：直接使用注解声明任务，使用时创建线程调用)
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();//调用异步的任务
        }

        sleep(10000);
    }

    // 6.Spring定时任务线程池(简化：注解不是任务了，他是自动执行，这里延时执行防止线程退出)
    @Test
    public void testThreadPoolTaskSchedulerSimple() {
        sleep(30000);
    }
}
