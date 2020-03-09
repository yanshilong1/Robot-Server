package com.example.rpc.server;

import com.example.rpc.Payload;
import com.example.rpc.PayloadTypes;

import java.util.UUID;

public class Register extends Payload {
    public String commandId;
    private String robotId;
    private String protocolVersion = "1.0.0";

    public Register() {
        commandId = UUID.randomUUID().toString();
        super.type = PayloadTypes.Register;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public String toString() {
        return "Register{" +
                "robotId='" + robotId + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
