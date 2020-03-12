package com.example.rpc;

/**
 * Inform消息中的事件类型
 *  Inform是初始化的消息类型、此消息类型中包含三种时间类型，机器人启动、机器人定时建立会话、机器人发送的心跳
 */
public enum EventTypes {
    BOOTSTRAP, // Robot启动
    SCHEDULE, // Robot定时建立会话
    HEARTBEAT, // Robot心跳
}
