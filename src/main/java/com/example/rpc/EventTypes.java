package com.example.rpc;

/**
 * Inform消息中的事件类型
 */
public enum EventTypes {
    BOOTSTRAP, // Robot启动
    SCHEDULE, // Robot定时建立会话
    HEARTBEAT, // Robot心跳
}
