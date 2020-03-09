package com.example;

import com.example.robot.Client;
import com.example.server.Server;

import java.io.IOException;

/**
 * 启动类。
 */
public class Bootstrap {
    public static void main(String[] args) throws InterruptedException, IOException {
        if ("client".equalsIgnoreCase(args[0])) {
            Client.main(args);
        } else if ("server".equalsIgnoreCase(args[0])) {
            Server.main(args);
        }
    }
}
