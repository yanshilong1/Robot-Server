package com.example.entity.rpc.client;

import com.example.entity.rpc.Payload;

import java.util.UUID;

public class SetConfiguration extends Payload {
    public String commandId;
    public String robotId;

    public SetConfiguration() {
        commandId = UUID.randomUUID().toString();
    }

    public int heartbeatInterval;
}
