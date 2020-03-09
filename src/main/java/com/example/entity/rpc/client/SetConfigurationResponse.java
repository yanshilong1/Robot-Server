package com.example.entity.rpc.client;

import com.example.entity.rpc.Payload;
import com.example.entity.rpc.ResponseStatus;

public class SetConfigurationResponse extends Payload {
    public String commandId;
    public String robotId;
    private ResponseStatus status;

    public SetConfigurationResponse() {

    }
}
