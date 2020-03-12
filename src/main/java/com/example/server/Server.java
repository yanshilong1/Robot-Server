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
        //声明一个线程池
        ExecutorService executor = null;
        //启动一个serverSocket,绑定一个端口号9000
        //此处使用了try()jdk1.7新加try-with-resources语法，能够自动释放资源
        try (ServerSocket serverSocket = new ServerSocket(ServerCfg.portNumber);) {
            //使用固定大小的fixedThreadPool,线程池大小为5(ExecutorService默认提供了四种线程池)
            executor = Executors.newFixedThreadPool(5);
            logger.info("Waiting for clients on port: " + ServerCfg.portNumber);
            while (true) {
                //第一个阻塞，serverSocket.accept()等待客户端连接
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

