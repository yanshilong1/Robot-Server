package com.example;

import com.example.MessageUtils;
import com.example.entity.rpc.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class MessageIOUtils {
    public static void write(OutputStream out, Message msg) throws IOException {
        out.write(MessageUtils.robotMsgToTcpByte(msg));
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
        return MessageUtils.byte4ToInt(length);
    }

    public static Message readMessage(InputStream in, int msgLength) throws IOException {
        byte[] msg = new byte[msgLength];
        read(in, msg);
        return MessageUtils.serverTcpByteToMessage(msg);
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
}
