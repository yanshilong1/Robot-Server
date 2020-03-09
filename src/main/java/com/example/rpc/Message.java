package com.example.rpc;

public class Message {
    public final static byte[] MAGIC_NUMBER = {1, 2, 3, 4};
    public final static byte[] MESSAGE_VERSION = {1};

    public PayloadTypes type;
    public String payload;

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", payload='" + payload + '\'' +
                '}';
    }
}
