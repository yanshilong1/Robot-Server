package com.example.entity.rpc.client;

import com.example.entity.rpc.Payload;
import com.example.entity.rpc.PayloadTypes;

import java.util.UUID;

public class Diagnostic extends Payload {
    public String commandId;
    public String robotId;

    public Diagnostic() {
        commandId = UUID.randomUUID().toString();
        super.type = PayloadTypes.Diagnostic;
    }
}
