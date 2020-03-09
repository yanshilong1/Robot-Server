package com.example.server;

import com.example.rpc.Message;
import com.example.rpc.Payload;
import com.example.utils.PayloadUtils;
import com.example.rpc.server.Inform;
import com.example.utils.MessageIOUtils;
import com.example.server.service.BaseService;
import com.example.server.service.DeployCfgService;
import com.example.server.service.HeartbeatService;
import com.example.server.service.RegisterService;

import java.io.*;

import java.net.Socket;

public class SessionHandler extends BaseService implements Runnable {
    public boolean someCfgToRobot = false;

    private Phase phase = Phase.INIT;
    private Socket client;
    private BaseService service;
//    ServerSocket serverSocket = null;

    private enum Phase {
        INIT, PROCESS
    }

    public SessionHandler(Socket client) {
        System.out.println("Server构造SessionHandler");
        this.client = client;
    }

    @Override
    public Payload process(Payload request) {
        if (phase == Phase.INIT) {
            service = selectService(request);
            System.out.println("Server选择service: " + service.getClass().getSimpleName());
            phase = Phase.PROCESS;
        }
        return service.process(request);
    }

    private BaseService selectService(Payload request) {
        BaseService service;
        Inform req = request.castAs(Inform.class);
        switch (req.getEvent()) {
            case BOOTSTRAP:
                service = new RegisterService();
                break;
            case HEARTBEAT:
                service = new HeartbeatService();
                break;
            case SCHEDULE:
                service = new DeployCfgService();
                break;
            default:
                throw new RuntimeException("Unknown Event: " + req.getEvent());
        }
        return service;
    }

    @Override
    public void run() {
        try (InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()) {
            while (true) {
                Message msg = MessageIOUtils.read(in);
                System.out.println("server接收：" + msg);
                Payload reqPayload = PayloadUtils.toPayload(msg);
                Payload respPayload = process(reqPayload);
                Message resp = PayloadUtils.toMessage(respPayload);
                System.out.println("server发送：" + resp);
                MessageIOUtils.write(out, resp);
            }
        } catch (IOException e) {
            System.out.println("I/O exception: " + e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Session closed");
    }

//    private Message read(InputStream in) throws IOException {
//        readMagicNum(in);
//        readVersion(in);
//        int msgLength = readLength(in);
//        return readMessage(in, msgLength);
//    }
//
//    private void write(OutputStream out, Message msg) throws IOException {
//        out.write(MessageUtils.serverMsgToTcpByte(msg));
//        out.flush();
//    }
//
//    private void readMagicNum(InputStream in) throws IOException {
//        byte[] magicNum = new byte[4];
//        read(in, magicNum);
//        checkMagicNum(magicNum);
//    }
//
//    private void checkMagicNum(byte[] data) throws IOException {
//        boolean success = Arrays.equals(Message.MAGIC_NUMBER, data);
//        if(!success) {
//            throw new IOException("Bad Data");
//        }
//    }
//
//    private byte[] readVersion(InputStream in) throws IOException {
//        byte[] version = new byte[1];
//        read(in, version);
//        return version;
//    }
//
//    private int readLength(InputStream in) throws IOException {
//        byte[] length = new byte[4];
//        read(in, length);
//        return MessageUtils.byte4ToInt(length);
//    }
//
//    private Message readMessage(InputStream in, int msgLength) throws IOException {
//        byte[] msg = new byte[msgLength];
//        read(in, msg);
//        return MessageUtils.serverTcpByteToMessage(msg);
//    }
//
//    private void read(InputStream in, byte[] data) throws IOException {
//        int dataLength = data.length;
//        int sumLength = 0;
//
//        while (sumLength < dataLength) {
//            int length = in.read(data, sumLength, dataLength - sumLength);
//            if (length == -1) {
//                throw new IOException("Bad Data");
//            }
//            sumLength += length;
//        }
//    }
}


