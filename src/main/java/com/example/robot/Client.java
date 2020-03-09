package com.example.robot;

import com.example.robot.service.HeartbeatService;
import com.example.robot.service.RegisterService;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Status status = Status.BOOTSTRAP;

    private enum Status {
        BOOTSTRAP, REGISTERED
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("robot main start");
        register();
        HeartbeatService.start();
        try {
            Thread.sleep(500000);
        } finally {
            HeartbeatService.stop();
        }

        System.out.println("robot main exit");
    }

    private static void register() throws InterruptedException {
        Thread registerThread = new Thread(new RegisterService());
        registerThread.start();
        registerThread.join();
    }
}

