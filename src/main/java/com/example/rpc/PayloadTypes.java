package com.example.rpc;

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
