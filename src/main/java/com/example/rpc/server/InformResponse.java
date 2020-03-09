package com.example.rpc.server;

import com.example.rpc.Payload;
import com.example.rpc.PayloadTypes;

public class InformResponse extends Payload {
    public String commandId;
    public String robotId;

    public InformResponse() {}

    public InformResponse(String commandId) {
        this.commandId = commandId;
        super.type = PayloadTypes.InformResponse;
    }
}
