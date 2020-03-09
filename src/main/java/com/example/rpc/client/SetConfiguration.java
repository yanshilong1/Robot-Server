package com.example.rpc.client;

import com.example.rpc.Payload;

import java.util.UUID;

public class SetConfiguration extends Payload {
    public String commandId;
    public String robotId;

    public SetConfiguration() {
        commandId = UUID.randomUUID().toString();
    }

    public int heartbeatInterval;
}
