package com.example.rpc.server;

import com.example.rpc.EventTypes;
import com.example.rpc.Payload;
import com.example.rpc.PayloadTypes;

import java.util.UUID;

public class Inform extends Payload {
    //会话id uuid
    private String commandId;
    private String robotId;
    //每次手动设置事件类型
    private EventTypes event;

    public Inform() {
        commandId = UUID.randomUUID().toString();
        super.type = PayloadTypes.Inform;
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

    public EventTypes getEvent() {
        return event;
    }

    public void setEvent(EventTypes event) {
        this.event = event;
    }
}
