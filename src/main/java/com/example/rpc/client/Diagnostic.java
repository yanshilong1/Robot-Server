package com.example.rpc.client;

import com.example.rpc.Payload;
import com.example.rpc.PayloadTypes;

import java.util.UUID;

public class Diagnostic extends Payload {
    public String commandId;
    public String robotId;

    public Diagnostic() {
        commandId = UUID.randomUUID().toString();
        super.type = PayloadTypes.Diagnostic;
    }
}
