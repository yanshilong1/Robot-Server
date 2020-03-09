package com.example.server;

import com.example.entity.Robot;

import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class Registry {
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
        System.out.println("已注册Robot: " + list().stream().map(Robot::getId).collect(Collectors.toList()));
    }
}