package org.example.message;

import cn.hutool.core.convert.Convert;

public enum BGPType {
    OPEN(1),
    UPDATE(2),
    NOTIFICATION(3),
    KEEPALIVE(4),
    UNKNOWN(0);

    private final int value;

    BGPType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
