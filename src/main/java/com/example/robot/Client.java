package com.example.robot;

import com.example.robot.service.HeartbeatService;
import com.example.robot.service.RegisterService;
import com.example.utils.PgpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("robot main start");
        register();
        HeartbeatService.start();
        try {
            doBusiness();
        } finally {
            HeartbeatService.stop();
        }
        logger.info("robot main exit");
    }

    /**
     * 客户端业务逻辑
     * @throws InterruptedException
     */
    private static void doBusiness() throws InterruptedException {
        Thread.sleep(500000);
    }

    private static void register() throws InterruptedException {
        Thread registerThread = new Thread(new RegisterService());
        registerThread.start();
        registerThread.join();
    }
}

