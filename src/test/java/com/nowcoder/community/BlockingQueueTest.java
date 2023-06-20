package com.nowcoder.community;


import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTest {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue(10);
        new Thread(new thread3(blockingQueue)).start();
        new Thread(new thread4(blockingQueue)).start();
        new Thread(new thread4(blockingQueue)).start();
        new Thread(new thread4(blockingQueue)).start();//线程会在run方法结束后退出

        Thread.sleep(10000);
        System.out.println(blockingQueue.take());//为空是不会返回空，而是堵塞
    }


    Thread thread1 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                thread1.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1);
        }
    });

    Thread thread2 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                thread2.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(2);
        }
    });

    @Test
    public void test1() throws InterruptedException {
//        thread1.start();
//        System.out.println("thread1线程已启动："+thread1.isAlive());
//        thread2.start();
//        new Thread().sleep(20);
    }

}

class thread3 implements Runnable {
    BlockingQueue<Integer> blockingQueue;

    thread3(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                blockingQueue.put(i);
                System.out.println("生产者" + blockingQueue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class thread4 implements Runnable {
    BlockingQueue<Integer> blockingQueue;

    thread4(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {

        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("消费者" + blockingQueue.size());
                blockingQueue.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}