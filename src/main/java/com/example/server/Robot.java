package com.example.server;

import com.example.RobotCfg;

import java.time.LocalDateTime;

/**
 * 机器人类
 */
public class Robot {
    private String id; // 身份识别ID
    private String ip;
    private String connReqPort;
    private volatile LocalDateTime lastHeartbeatTime; // 最近一次心跳时间戳

    public Robot(String id) {
        this.id = id;
    }

    /**
     * 判断是否在线。如果连续三个心跳周期没有收到消息，则认为离线。
     * @return
     */
    public boolean isAlive() {
        LocalDateTime lastTime = lastHeartbeatTime;
        if (lastTime == null) {
            return false;
        }

        LocalDateTime deadline = lastTime.plusSeconds(RobotCfg.HEARTBEAT_INTERVAL * 3);
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(deadline);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getConnReqPort() {
        return connReqPort;
    }

    public void setConnReqPort(String connReqPort) {
        this.connReqPort = connReqPort;
    }

    public LocalDateTime getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(LocalDateTime lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }
}
