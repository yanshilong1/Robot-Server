package com.example.rpc.client;

import com.example.rpc.Payload;
import com.example.rpc.ResponseStatus;

public class SetConfigurationResponse extends Payload {
    public String commandId;
    public String robotId;
    private ResponseStatus status;

    public SetConfigurationResponse() {

    }
}
