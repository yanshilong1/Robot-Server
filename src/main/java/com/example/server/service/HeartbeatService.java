package com.example.server.service;

import com.example.server.Robot;
import com.example.rpc.Idle;
import com.example.rpc.Payload;
import com.example.rpc.server.Inform;
import com.example.rpc.server.InformResponse;
import com.example.server.Registry;

import java.time.LocalDateTime;

/**
 * 心跳服务
 */
public class HeartbeatService extends BaseService {

    private enum Phase {
        INIT, COMPLETE
    }

    private Phase phase = Phase.INIT;

    @Override
    public Payload process(Payload request) {
        Payload resp;
        switch (phase) {
            case INIT:
                resp = handleInform(request);
                break;
            case COMPLETE:
                resp = handleIdle(request);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + phase);
        }
        return resp;
    }

    /**
     * 处理Inform消息 处理心跳
     * @param request robotID
     * @return
     */
    private Payload handleInform(Payload request) {
        //强转为子类型
        Inform inform = request.castAs(Inform.class);
        String robotId = inform.getRobotId();
        //去服务发现的map里面寻找到发送心跳的robot----？如果这里找不到怎么办
        Robot robot = Registry.find(robotId);
        //更新robot的最新一次心跳时间
        robot.setLastHeartbeatTime(LocalDateTime.now());
        //心跳完成返回
        phase = Phase.COMPLETE;
        return new InformResponse(inform.getCommandId());
    }

    /**
     * 处理Idle消息
     * @param request
     * @return
     */
    private Payload handleIdle(Payload request) {
        Idle req = request.castAs(Idle.class);
        return new Idle(); // close session
    }
}
