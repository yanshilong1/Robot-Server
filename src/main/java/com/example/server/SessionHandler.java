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
 * 会话管理的实现类，继承了BaseService服务类
 *
 *  会话目前有两个状态 （会话建立and通信）状态
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
            //此处的while，是一个请求进来后，一直重复的与她进行数据交互
            while (true) {
                //socket读函数  实现功能：读取socket的内容并封装成为Message通用格式
                Message inputMsg = MessageIOUtils.read(in);
                logger.debug("server接收：" + inputMsg);
                //对消息进行分类，把通用对象转化为对应的子类型（注册？通信？等等）
                Payload inputPayload = PayloadUtils.toPayload(inputMsg);
                /**
                  * 业务处理函数，这里是整个项目业务的入口。
                  * @param inputPayload 客户端发送的请求参数
                  * @return outputPayload 返回一个通用对象
                 */
                Payload outputPayload = process(inputPayload);
                //通用对象转化为通用的Message格式，通过socket发送给客户端
                Message outputMsg = PayloadUtils.toMessage(outputPayload);
                MessageIOUtils.write(out, outputMsg);
                //至此，一次通信交互完成
                logger.debug("server发送：" + outputMsg);
            }
        } catch (Throwable e) {
            logger.warn("Session closed  握手完毕" + e.getMessage());
        }
    }

    /**
     * 业务处理入口
     * @param request
     * @return
     */
    @Override
    public Payload process(Payload request) {
        //如果是会话建立消息
        if (phase == Phase.INIT) {
            /**
             * 判断会话建立的子类型
             *     BOOTSTRAP, //Robot启动
             *     SCHEDULE, // Robot定时建立会话
             *     HEARTBEAT, // Robot心跳
             */
            service = selectService(request);
            //打印类的名称
            logger.debug("New Session start, service: " + service.getClass().getSimpleName());
            //设置为通信阶段
            phase = Phase.PROCESS;
        }
        //通信消息，直接进行业务处理
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


