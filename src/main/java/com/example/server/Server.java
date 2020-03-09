package com.example.server;

import com.example.ServerCfg;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        System.out.println("Server start");
        ExecutorService executor = null;
        try (ServerSocket serverSocket = new ServerSocket(ServerCfg.portNumber);) {
            executor = Executors.newFixedThreadPool(5);
            System.out.println("Waiting for clients on port: " + ServerCfg.portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Runnable worker = new SessionHandler(clientSocket);
                executor.execute(worker);
            }
        } catch (Throwable e) {
            System.out.println("Server stop, error: " + e.getMessage());
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }
}

