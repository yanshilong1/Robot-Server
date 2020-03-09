package com.example.server.service;

import com.example.entity.Robot;
import com.example.entity.rpc.Idle;
import com.example.entity.rpc.Payload;
import com.example.entity.rpc.server.Inform;
import com.example.entity.rpc.server.InformResponse;
import com.example.server.Registry;

import java.time.LocalDateTime;

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

    private Payload handleInform(Payload request) {
        Inform inform = request.castAs(Inform.class);
        String robotId = inform.getRobotId();
        Robot robot = Registry.find(robotId);
        robot.setLastHeartbeatTime(LocalDateTime.now());
        phase = Phase.COMPLETE;
        return new InformResponse(inform.getCommandId());
    }

    private Payload handleIdle(Payload request) {
        Idle req = request.castAs(Idle.class);
        return new Idle();
    }
}
