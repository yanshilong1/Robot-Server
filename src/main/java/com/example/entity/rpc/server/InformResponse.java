package com.example.entity.rpc.server;

import com.example.entity.rpc.Payload;
import com.example.entity.rpc.PayloadTypes;
import com.example.entity.rpc.ResponseStatus;

public class InformResponse extends Payload {
    public String commandId;
    public String robotId;

    public InformResponse() {}

    public InformResponse(String commandId) {
        this.commandId = commandId;
        super.type = PayloadTypes.InformResponse;
    }
}
