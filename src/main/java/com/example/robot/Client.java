package com.example.robot;

import com.example.robot.service.HeartbeatService;
import com.example.robot.service.RegisterService;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("robot main start");
        register();
        HeartbeatService.start();
        try {
            doBusiness();
        } finally {
            HeartbeatService.stop();
        }
        System.out.println("robot main exit");
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

