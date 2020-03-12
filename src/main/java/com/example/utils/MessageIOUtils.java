package com.example.utils;

import com.alibaba.fastjson.JSON;
import com.example.rpc.Message;

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
 *
 * Message二进制格式为：
 *
 * [MagicNum(4byte)] + [version(1byte)] + [dataLength(4byte)] + [data(Message)]
 */
public class MessageIOUtils {
    public static void write(OutputStream out, Message msg) throws IOException {
        out.write(robotSerialize(msg));
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
        //帕努单两个数组是否相等
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

    /**
     * 从输入流中读出指定量数据。
     * @param in
     * @param data
     * @throws IOException
     */
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

    /**
     * 将Message对象序列化为二进制数据。
     * @param msg Message对象
     * @param encryptFunc 加密函数
     * @return 可传输的二进制数据
     */
    public static byte[] serialize(Message msg, Function<byte[], byte[]> encryptFunc) {
        byte[] msgByte = null;
        byte[] msgLengthByte = {0, 0, 0, 0};
        if (msg != null) {
            msgByte = JSON.toJSONString(msg).getBytes(StandardCharsets.UTF_8);
            msgByte = encryptFunc.apply(msgByte);
            msgLengthByte = positiveIntTo4Byte(msgByte.length);
        }
        return MiscUtils.mergeByteArray(Message.MAGIC_NUMBER, Message.MESSAGE_VERSION, msgLengthByte, msgByte);
    }

    /**
     * 客户端序列化Message
     * @param msg
     * @return
     */
    public static byte[] robotSerialize(Message msg) {
        return serialize(msg, PgpUtils::robotEncryptPGP);
    }

    /**
     * 服务端序列化Message
     * @param msg
     * @return
     */
    public static byte[] serverMsgToTcpByte(Message msg) {
        return serialize(msg, PgpUtils::serverEncryptPGP);
    }

    /**
     * 将二进制数据反序列化为Message对象
     * @param tcpByte
     * @param decryptFunc
     * @return
     */
    public static Message deserialize(byte[] tcpByte, Function<byte[], byte[]> decryptFunc) {
        tcpByte = decryptFunc.apply(tcpByte);
        String msgJson = new String(tcpByte, StandardCharsets.UTF_8);
        return JSON.parseObject(msgJson, Message.class);
    }

    /**
     * 客户端反序列化
     * @param tcpByte
     * @return
     */
    public static Message robotDeserialize(byte[] tcpByte) {
        return deserialize(tcpByte, PgpUtils::robotDecryptPGP);
    }

    /**
     * 服务端反序列化
     * @param tcpByte
     * @return
     */
    public static Message serverTcpByteToMessage(byte[] tcpByte) {
        return deserialize(tcpByte, PgpUtils::serverDecryptPGP);
    }

    /**
     * 将Int转换为4byte数据。
     * @param posInt
     * @return
     */
    private static byte[] positiveIntTo4Byte(int posInt) {
        return longTo4Byte(posInt);
    }

    /**
     * 将4byte数据转换为Int
     * @param bytes
     * @return
     */
    public static int byte4ToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    /**
     * 将long转换为4byte数据
     * @param value
     * @return
     */
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
