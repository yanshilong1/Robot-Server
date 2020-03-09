package com.example.utils;

import com.alibaba.fastjson.JSON;
import com.example.rpc.Message;
import com.example.rpc.PayloadTypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

/**
 * 处理Message消息的读写。
 */
public class MessageIOUtils {
    public static void write(OutputStream out, Message msg) throws IOException {
        out.write(robotMsgToTcpByte(msg));
        out.flush();
    }

    public static Message read(InputStream in) throws IOException {
        readMagicNum(in);
        readVersion(in);
        int msgLength = readLength(in);
        return readMessage(in, msgLength);
    }

    public static void readMagicNum(InputStream in) throws IOException {
        byte[] magicNum = new byte[4];
        read(in, magicNum);
        checkMagicNum(magicNum);
    }

    public static void checkMagicNum(byte[] data) throws IOException {
        boolean success = Arrays.equals(Message.MAGIC_NUMBER, data);
        if(!success) {
            throw new IOException("Bad Data");
        }
    }

    public static byte[] readVersion(InputStream in) throws IOException {
        byte[] version = new byte[1];
        read(in, version);
        return version;
    }

    public static int readLength(InputStream in) throws IOException {
        byte[] length = new byte[4];
        read(in, length);
        return byte4ToInt(length);
    }

    public static Message readMessage(InputStream in, int msgLength) throws IOException {
        byte[] msg = new byte[msgLength];
        read(in, msg);
        return serverTcpByteToMessage(msg);
    }

    public static void read(InputStream in, byte[] data) throws IOException {
        int dataLength = data.length;
        int sumLength = 0;

        while (sumLength < dataLength) {
            int length = in.read(data, sumLength, dataLength - sumLength);
            if (length == -1) {
                throw new IOException("I/O Closed");
            }
            sumLength += length;
        }
    }


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
}
