package com.example.entity.rpc.server;

import com.example.entity.rpc.EventTypes;
import com.example.entity.rpc.Payload;
import com.example.entity.rpc.PayloadTypes;

import java.util.UUID;

public class Inform extends Payload {
    private String commandId;
    private String robotId;
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
