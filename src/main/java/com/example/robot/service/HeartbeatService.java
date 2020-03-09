package com.example.robot.service;

import com.example.rpc.EventTypes;
import com.example.rpc.Idle;
import com.example.rpc.Message;
import com.example.utils.MessageIOUtils;
import com.example.utils.PayloadUtils;
import com.example.RobotCfg;
import com.example.ServerCfg;
import com.example.rpc.server.Inform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatService implements Runnable {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static final HeartbeatService INSTANCE = new HeartbeatService();

    public static void start() {
        executor.scheduleAtFixedRate(INSTANCE, 0, 30, TimeUnit.SECONDS);
    }

    public static void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(5000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(ServerCfg.hostName, ServerCfg.portNumber);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            process(in, out);
        } catch (IOException e) {
            System.out.println("心跳失败: " + e.getMessage());
        }
    }

    private void process(InputStream in, OutputStream out) throws IOException {
        // 发Inform
        Inform inform = new Inform();
        inform.setEvent(EventTypes.HEARTBEAT);
        inform.setRobotId(RobotCfg.ROBOT_ID);
        Message msg = PayloadUtils.toMessage(inform);
        System.out.println("robot发送: " + msg);
        MessageIOUtils.write(out, msg);

        // 收InformResponse
        msg = MessageIOUtils.read(in);
        System.out.println("robot接收: " + msg);

        // 发Idle
        msg = PayloadUtils.toMessage(new Idle());
        System.out.println("robot发送: " + msg);
        MessageIOUtils.write(out, msg);

        // 收Idle
        msg = MessageIOUtils.read(in);
        System.out.println("robot发送: " + msg);
        System.out.println("心跳成功");
    }

}
