package com.example.server;

import com.example.rpc.Message;
import com.example.rpc.Payload;
import com.example.rpc.server.Inform;
import com.example.server.service.BaseService;
import com.example.server.service.DeployCfgService;
import com.example.server.service.HeartbeatService;
import com.example.server.service.RegisterService;
import com.example.utils.MessageIOUtils;
import com.example.utils.PayloadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 会话处理
 */
public class SessionHandler extends BaseService implements Runnable {

    private static final Logger logger = LogManager.getLogger(SessionHandler.class);

    private Phase phase = Phase.INIT;
    private Socket client;
    private BaseService service;

    /**
     * 状态枚举
     */
    private enum Phase {
        INIT, // 会话建立阶段
        PROCESS // 通信处理阶段
    }

    public SessionHandler(Socket client) {
        this.client = client;
    }

    /**
     * 通信入口方法
     */
    @Override
    public void run() {
        try (InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()) {
            while (true) {
                Message inputMsg = MessageIOUtils.read(in);
                logger.debug("server接收：" + inputMsg);
                Payload inputPayload = PayloadUtils.toPayload(inputMsg);
                Payload outputPayload = process(inputPayload);
                Message outputMsg = PayloadUtils.toMessage(outputPayload);
                MessageIOUtils.write(out, outputMsg);
                logger.debug("server发送：" + outputMsg);
            }
        } catch (Throwable e) {
            logger.warn("Session closed " + e.getMessage());
        }
    }

    /**
     * 业务处理入口
     * @param request
     * @return
     */
    @Override
    public Payload process(Payload request) {
        if (phase == Phase.INIT) {
            service = selectService(request);
            logger.debug("New Session start, service: " + service.getClass().getSimpleName());
            phase = Phase.PROCESS;
        }
        return service.process(request);
    }

    /**
     * 根据Inform消息中的事件类型选择业务类。
     * @param request
     * @return
     */
    private BaseService selectService(Payload request) {
        BaseService service;
        Inform req = request.castAs(Inform.class);
        switch (req.getEvent()) {
            case BOOTSTRAP:
                service = new RegisterService();
                break;
            case HEARTBEAT:
                service = new HeartbeatService();
                break;
            case SCHEDULE:
                service = new DeployCfgService();
                break;
            default:
                throw new RuntimeException("Unknown Event: " + req.getEvent());
        }
        return service;
    }
}


