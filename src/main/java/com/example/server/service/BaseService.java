package com.example.server.service;

import com.example.rpc.Payload;

/**
 * 基础服务类
 */
public abstract class BaseService {
    protected String reqCommandId; // 请求消息ID

    public abstract Payload process(Payload request);

    /**
     * 用于检查请求消息与回复消息中的commandId是否相同。
     * @param respCommandId
     */
    protected void checkCmdId(String respCommandId) {
        if (!reqCommandId.equals(respCommandId)) {
            throw new RuntimeException("command id error");
        }
    }
}
