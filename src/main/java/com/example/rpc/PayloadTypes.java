package com.example.rpc;

/**
 * 负载类型定义
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
