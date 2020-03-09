package com.example.rpc.server;

import com.example.rpc.Payload;
import com.example.rpc.PayloadTypes;
import com.example.rpc.ResponseStatus;

public class RegisterResponse extends Payload {
    public String commandId;
    public String robotId;
    private ResponseStatus status;
    private String desc;

    public RegisterResponse() {}

    public RegisterResponse(String commandId, ResponseStatus status, String desc) {
        super.type = PayloadTypes.RegisterResponse;
        this.commandId = commandId;
        this.status = status;
        this.desc = desc;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
