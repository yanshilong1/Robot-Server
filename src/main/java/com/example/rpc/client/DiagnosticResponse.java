package com.example.rpc.client;

import com.example.rpc.Payload;
import com.example.rpc.PayloadTypes;


public class DiagnosticResponse extends Payload {
    private String commandId;
    private String robotId;
    private String ip;
    private String connReqPort;

    // env
    private String os;
    private String memory;

    public DiagnosticResponse() {

    }

    public DiagnosticResponse(String commandId) {
        this.commandId = commandId;
        super.type = PayloadTypes.DiagnosticResponse;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getConnReqPort() {
        return connReqPort;
    }

    public void setConnReqPort(String connReqPort) {
        this.connReqPort = connReqPort;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "DiagnosticResponse{" +
                "commandId='" + commandId + '\'' +
                ", robotId='" + robotId + '\'' +
                ", ip='" + ip + '\'' +
                ", connReqPort='" + connReqPort + '\'' +
                ", os='" + os + '\'' +
                ", memory='" + memory + '\'' +
                '}';
    }
}
