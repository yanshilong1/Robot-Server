package com.example.server.service;

import com.example.rpc.Idle;
import com.example.rpc.Payload;
import com.example.rpc.client.SetConfiguration;
import com.example.rpc.client.SetConfigurationResponse;
import com.example.rpc.server.Inform;
import com.example.rpc.server.InformResponse;

public class DeployCfgService extends BaseService {
    private enum Phase {
        INIT, DEPLOY, COMPLETE
    }

//    private Inform inform;
    private Phase phase = Phase.INIT;

    @Override
    public Payload process(Payload req) {
        Payload resp;
        switch (phase) {
            case INIT:
                resp = handleInform(req);
                break;
            case DEPLOY:
                resp = handleIdleAndCallSetCfg(req);
                break;
            case COMPLETE:
                resp = handleSetCfgResp(req);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + phase);
        }
        return resp;
    }

    private Payload handleInform(Payload payload) {
        Inform inform = payload.castAs(Inform.class);
        phase = Phase.DEPLOY;
        return new InformResponse(inform.getCommandId());
    }

    private Payload handleIdleAndCallSetCfg(Payload payload) {
        Idle req = payload.castAs(Idle.class);
        SetConfiguration setCfgReq = new SetConfiguration();
        setCfgReq.heartbeatInterval = 20;
        super.reqCommandId = setCfgReq.commandId;
        phase = Phase.COMPLETE;
        return setCfgReq;
    }

    private Payload handleSetCfgResp(Payload payload) {
        SetConfigurationResponse resp = payload.castAs(SetConfigurationResponse.class);
        super.checkCmdId(resp.commandId);
        System.out.println(resp);
        return new Idle();
    }
}
