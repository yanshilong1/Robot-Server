package com.example.robot;

import com.example.robot.service.HeartbeatService;
import com.example.robot.service.RegisterService;
import com.example.utils.PgpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable{
    private static final Logger logger = LogManager.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService= Executors.newFixedThreadPool(100);
        for(int i=0;i<1000;i++){
            logger.debug("线程-------------"+i+"-------启动");
            executorService.execute(new Client());
        }
    }

    @Override
    public void run() {
        for (int i=0;i<100;i++) {
            logger.info("robot main start");
            try {
                register();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HeartbeatService.start();
            try {
                //模拟客户端逻辑
                try {
                    doBusiness();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                HeartbeatService.stop();
            }
        }
        logger.info("robot main exit");
    }



    /**
     * 客户端业务逻辑
     * @throws InterruptedException
     */
    private static void doBusiness() throws InterruptedException {
        for(int i=0;i<10;i++) {
            Thread.sleep(100);
            logger.info("执行业务逻辑："+i);
        }
    }




    private static void register() throws InterruptedException {
        //启动一个线程去执行注册逻辑，并且主线程阻塞等待注册完成
        Thread registerThread = new Thread(new RegisterService());
        logger.debug("开始注册");
        registerThread.start();
        //join作用是等待registerThread执行完毕
        registerThread.join();
        logger.info("注册完成");
    }
}

