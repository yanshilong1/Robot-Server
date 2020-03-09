package com.example.server;

import com.example.ServerCfg;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("Server start");
        ExecutorService executor = null;
        try (ServerSocket serverSocket = new ServerSocket(ServerCfg.portNumber);) {
            executor = Executors.newFixedThreadPool(5);
            logger.info("Waiting for clients on port: " + ServerCfg.portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Runnable worker = new SessionHandler(clientSocket);
                executor.execute(worker);
            }
        } catch (Throwable e) {
            logger.error("Server stop, error: ", e);
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }
}

