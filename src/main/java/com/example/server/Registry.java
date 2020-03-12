package com.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * 客户端注册管理
 */
public class Registry {
    private static final Logger logger = LogManager.getLogger(Registry.class);

    //服务发现的map,key=rebotID  value rebot对象
    private static final ConcurrentSkipListMap<String, Robot> map = new ConcurrentSkipListMap<>();

    public static boolean register(Robot robot) {
        return map.putIfAbsent(robot.getId(), robot) == null;
    }

    public static Robot find(String id) {
        return map.get(id);
    }

    public static Collection<Robot> list() {
        return map.values();
    }

    public static void print() {
        List<String> clientIdList = list()
                .stream()
                .map(Robot::getId)
                .collect(Collectors.toList());
        logger.info("已注册Robot: " + clientIdList);
    }
}
