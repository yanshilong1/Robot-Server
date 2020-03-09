package com.example.server.service;

import com.example.rpc.Payload;

public abstract class BaseService {
    protected String commandId;

    public abstract Payload process(Payload request);

    protected void checkCmdId(String curId) {
        if (!commandId.equals(curId)) {
            throw new RuntimeException("command id error");
        }
    }
}
