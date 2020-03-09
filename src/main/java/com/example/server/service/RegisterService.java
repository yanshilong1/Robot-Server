package com.example.server.service;

import com.example.server.Robot;
import com.example.rpc.Idle;
import com.example.rpc.Payload;
import com.example.rpc.ResponseStatus;
import com.example.rpc.client.Diagnostic;
import com.example.rpc.client.DiagnosticResponse;
import com.example.rpc.server.Inform;
import com.example.rpc.server.InformResponse;
import com.example.rpc.server.Register;
import com.example.rpc.server.RegisterResponse;
import com.example.server.Registry;

public class RegisterService extends BaseService {
    private enum Phase {
        INIT, REGISTER, DIAGNOSTIC, COMPLETE
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
            case REGISTER:
                resp = handleRegister(req);
                break;
            case DIAGNOSTIC:
                resp = handleIdleAndCallDiag(req);
                break;
            case COMPLETE:
                resp = handleDiagResp(req);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + phase);
        }
        return resp;
    }

    private Payload handleInform(Payload payload) {
        Inform inform = payload.castAs(Inform.class);
        phase = Phase.REGISTER;
        return new InformResponse(inform.getCommandId());
    }

    private Payload handleRegister(Payload payload) {
        Register req = payload.castAs(Register.class);
        boolean success = Registry.register(new Robot(req.getRobotId()));
        Registry.print();
        ResponseStatus repStatus = success ? ResponseStatus.SUCCESS : ResponseStatus.FAILED;
        phase = Phase.DIAGNOSTIC;
        return new RegisterResponse(req.commandId, repStatus, "");
    }

    private Payload handleIdleAndCallDiag(Payload payload) {
        Idle req = payload.castAs(Idle.class);
        phase = Phase.COMPLETE;
        Diagnostic diagReq = new Diagnostic();
        super.commandId = diagReq.commandId;
        return diagReq;
    }

    private Payload handleDiagResp(Payload payload) {
        DiagnosticResponse resp = payload.castAs(DiagnosticResponse.class);
        super.checkCmdId(resp.getCommandId());

        String robotId = resp.getRobotId();
        Robot robot = Registry.find(robotId);
        robot.setIp(resp.getIp());
        robot.setConnReqPort(resp.getConnReqPort());
        return new Idle();
    }
}
