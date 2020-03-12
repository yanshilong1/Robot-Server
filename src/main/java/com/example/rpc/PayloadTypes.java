package com.example.rpc;

/**
 * 负载类型定义
 */

/**
 * 我的理解，这里是消息的类型枚举
 *  消息类型分为心跳类型
 *              注册消息--->注册消息的resopnse
 *              诊断消息--->诊断消息的response
 *              配置消息-->配置消息的回复
 */
public enum PayloadTypes {
    // Server
    Inform,
    InformResponse,
    Register,
    RegisterResponse,

    // Robot
    Diagnostic,
    DiagnosticResponse,
    SetConfiguration,
    SetConfigurationResponse,

    //
    Idle
}
