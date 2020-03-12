package com.example.robot.service;

import com.example.robot.Client;
import com.example.rpc.EventTypes;
import com.example.rpc.Idle;
import com.example.rpc.Message;
import com.example.utils.MessageIOUtils;
import com.example.utils.PayloadUtils;
import com.example.RobotCfg;
import com.example.ServerCfg;
import com.example.rpc.server.Inform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 心跳服务。
 */
public class HeartbeatService implements Runnable {

    private static final Logger logger = LogManager.getLogger(Client.class);

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static final HeartbeatService INSTANCE = new HeartbeatService();

    /**
     * 启动心跳服务
     */
    public static void start() {
        executor.scheduleAtFixedRate(INSTANCE, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 停止心跳服务
     */
    public static void stop() {
        executor.shutdown();
        //关闭线程池
        try {
            //最长关闭等待时机那
            executor.awaitTermination(5000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void run() {
        logger.info("心跳run方法开始--------------");
        try (Socket socket = new Socket(ServerCfg.hostName, ServerCfg.portNumber);

             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            process(in, out);
            logger.info("心跳run方法结束--------------");
        } catch (IOException e) {
            logger.warn("心跳失败", e);
        }
    }

    /**
     * 处理心跳流程
     * @param in
     * @param out
     * @throws IOException
     */
    private void process(InputStream in, OutputStream out) throws IOException {
        // 发Inform
        Inform inform = new Inform();
        inform.setEvent(EventTypes.HEARTBEAT);
        inform.setRobotId(RobotCfg.ROBOT_ID);
        Message msg = PayloadUtils.toMessage(inform);
        logger.debug("robot心跳发送Inform: " + msg);
        MessageIOUtils.write(out, msg);

        // 收InformResponse
        msg = MessageIOUtils.read(in);
        logger.debug("robot心跳接收收InformResponse: " + msg);

        // 发Idle
        msg = PayloadUtils.toMessage(new Idle());
        logger.debug("robot心跳发送Idle : " + msg);
        MessageIOUtils.write(out, msg);

        // 收Idle
        msg = MessageIOUtils.read(in);
        logger.debug("robot心跳接收Idle: " + msg);
        logger.debug("心跳成功");
    }

}
