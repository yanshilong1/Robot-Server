package com.example;

import com.alibaba.fastjson.JSON;
import com.example.entity.rpc.Message;
import com.example.entity.rpc.PayloadTypes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class MessageUtils {

//    private final static long MESSAGE_MAX_LENGTH = 0xFFFFFFFFL; // in byte

    public static byte[] msgToTcpByte(Message msg, Function<byte[], byte[]> encryptFunc) {
        byte[] msgByte = null;
        byte[] msgLengthByte = {0, 0, 0, 0};
        if (msg != null) {
            msgByte = JSON.toJSONString(msg).getBytes(StandardCharsets.UTF_8);
            msgByte = encryptFunc.apply(msgByte);
            msgLengthByte = positiveIntTo4Byte(msgByte.length);
        }
        return MiscUtils.mergeByteArray(Message.MAGIC_NUMBER, Message.MESSAGE_VERSION, msgLengthByte, msgByte);
    }

    public static byte[] robotMsgToTcpByte(Message msg) {
        return msgToTcpByte(msg, PgpUtils::robotEncryptPGP);
    }

    public static byte[] serverMsgToTcpByte(Message msg) {
        return msgToTcpByte(msg, PgpUtils::serverEncryptPGP);
    }

    public static Message tcpByteToMessage(byte[] tcpByte, Function<byte[], byte[]> decryptFunc) {
//        byte[] msgLengthByte = new byte[4];
//        System.arraycopy(tcpByte, 5, msgLengthByte, 0, 4);
//        int msgLength = byte4ToInt(msgLengthByte);
//        byte[] msgByte = new byte[msgLength];
//        System.arraycopy(tcpByte, 9, msgByte, 0, msgLength);
        tcpByte = decryptFunc.apply(tcpByte);
        String msgJson = new String(tcpByte, StandardCharsets.UTF_8);
        return JSON.parseObject(msgJson, Message.class);
    }

    public static Message robotTcpByteToMessage(byte[] tcpByte) {
        return tcpByteToMessage(tcpByte, PgpUtils::robotDecryptPGP);
    }

    public static Message serverTcpByteToMessage(byte[] tcpByte) {
        return tcpByteToMessage(tcpByte, PgpUtils::serverDecryptPGP);
    }


    private static byte[] positiveIntTo4Byte(int posInt) {
        return longTo4Byte(posInt);
    }

    public static int byte4ToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    private static byte[] longTo4Byte(long value) {
        byte[] data = new byte[4];
        data[3] = (byte) value;
        data[2] = (byte) (value >>> 8);
        data[1] = (byte) (value >>> 16);
        data[0] = (byte) (value >>> 24);
        return data;
    }

    private static long byte4ToLong(byte[] bytes) {
        return byte4ToInt(bytes) & 0xFFFFFFFFL;
    }

    public static void main(String[] args) {
        Message msg = new Message();
        msg.type = PayloadTypes.Register;
//        msg.commandId = "123";
        msg.payload = "456";

        byte[] tcpByte = robotMsgToTcpByte(msg);
        msg = serverTcpByteToMessage(tcpByte);

        System.out.println(msg.type);
//        System.out.println(msg.commandId);
        System.out.println(msg.payload);

        tcpByte = serverMsgToTcpByte(msg);
        msg = robotTcpByteToMessage(tcpByte);

        System.out.println(msg.type);
//        System.out.println(msg.commandId);
        System.out.println(msg.payload);
    }

}
