package com.example;

import java.util.UUID;

/**
 * 机器人配置信息
 */
public class RobotCfg {
    public static final String ROBOT_ID = UUID.randomUUID().toString();
    public static final int HEARTBEAT_INTERVAL = 60; // 心跳周期（秒）
}
