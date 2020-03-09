package com.example;

import com.alibaba.fastjson.JSON;
import com.example.entity.rpc.Idle;
import com.example.entity.rpc.Message;
import com.example.entity.rpc.Payload;
import com.example.entity.rpc.PayloadTypes;
import com.example.entity.rpc.client.Diagnostic;
import com.example.entity.rpc.client.DiagnosticResponse;
import com.example.entity.rpc.client.SetConfiguration;
import com.example.entity.rpc.client.SetConfigurationResponse;
import com.example.entity.rpc.server.Inform;
import com.example.entity.rpc.server.InformResponse;
import com.example.entity.rpc.server.Register;
import com.example.entity.rpc.server.RegisterResponse;

public class PayloadUtils {
    public static Payload toPayload(Message msg) {
        Payload payload;
        String payloadJson = msg.payload;
        PayloadTypes payloadType = msg.type;
        switch (payloadType) {
            case Inform:
                payload = JSON.parseObject(payloadJson, Inform.class);
                break;
            case InformResponse:
                payload = JSON.parseObject(payloadJson, InformResponse.class);
                break;
            case Register:
                payload = JSON.parseObject(payloadJson, Register.class);
                break;
            case RegisterResponse:
                payload = JSON.parseObject(payloadJson, RegisterResponse.class);
                break;
            case Diagnostic:
                payload = JSON.parseObject(payloadJson, Diagnostic.class);
                break;
            case DiagnosticResponse:
                payload = JSON.parseObject(payloadJson, DiagnosticResponse.class);
                break;
            case SetConfiguration:
                payload = JSON.parseObject(payloadJson, SetConfiguration.class);
                break;
            case SetConfigurationResponse:
                payload = JSON.parseObject(payloadJson, SetConfigurationResponse.class);
                break;
            case Idle:
                payload = JSON.parseObject(payloadJson, Idle.class);
                break;
            default:
                throw new RuntimeException();
        }
        return payload;
    }

    public static Message toMessage(Payload payload) {
        Message msg = new Message();
        msg.type = payload.type;
        msg.payload = JSON.toJSONString(payload);
        return msg;
    }
}
