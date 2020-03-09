package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * 初始化启动类
 */
@PropertySources({
        @PropertySource(value = {"classpath:server.properties"}, encoding = "utf-8"),
        @PropertySource(value = {"classpath:client.properties"}, encoding = "utf-8")
})
@SpringBootApplication
public class RobotApplication {
    public static void main(String[] args) {
        SpringApplication.run(RobotApplication.class, args);
    }
}
