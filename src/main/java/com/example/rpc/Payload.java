package com.example.rpc;

public class Payload {
    public PayloadTypes type;

    @SuppressWarnings("unchecked")
    public <T> T castAs(Class<T> clazz) {
        if (this.getClass() != clazz) {
            throw new RuntimeException("Payload cast failed, expect: " + clazz.getSimpleName());
        }
        return (T) this;
    }

    public boolean checkType(Class<?> clazz) {
        return this.getClass() == clazz;
    }
}
