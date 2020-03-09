package com.example.entity;

import java.time.LocalDateTime;

public class Robot {
    public static final int HEARTBEAT_INTERVAL = 60; // 心跳周期（秒）

    private String id;
    private String ip;
    private String connReqPort;
    private volatile LocalDateTime lastHeartbeatTime;

    public Robot(String id) {
        this.id = id;
    }

    public Boolean isAlive() {
        LocalDateTime lastTime = lastHeartbeatTime;
        if (lastTime == null) {
            return null;
        }

        LocalDateTime deadline = lastTime.plusSeconds(HEARTBEAT_INTERVAL * 3);
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
