package com.example.rpc;

/**
 * 通信消息的定义
 *  协议的定义分为两部分 类型数据+消息体数据
 *  类型数据枚举:
 *  @see com.example.rpc.PayloadTypes
 *
 *  实例数据为一个String类型的数据
 */
public class Message {
    public final static byte[] MAGIC_NUMBER = {1, 2, 3, 4};
    public final static byte[] MESSAGE_VERSION = {1};

    public PayloadTypes type; // 负载数据类型
    public String payload; // 负载数据

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", payload='" + payload + '\'' +
                '}';
    }
}
