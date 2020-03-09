package com.example.rpc;

/**
 * Message类的负载部分。
 */
public class Payload {
    public PayloadTypes type;

    /**
     * 强转为子类型
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T castAs(Class<T> clazz) {
        if (this.getClass() != clazz) {
            throw new RuntimeException("Payload cast failed, expect: " + clazz.getSimpleName());
        }
        return (T) this;
    }

    /**
     * 检查是否为指定类型
     * @param clazz
     * @return
     */
    public boolean checkType(Class<?> clazz) {
        return this.getClass() == clazz;
    }
}
