package org.example.message.notification;

public enum BGPNotificationErrorCode {

    MessageHeaderError(1),
    OPENMessageError(2),
    UPDATEMessageError(3),
    HoldTimerExpired(4),
    FiniteStateMachineError(5),
    Cease(6);

    private final int value;

    BGPNotificationErrorCode(int value) {
        this.value = value;
    }

    // 根据值创建枚举类
    public static BGPNotificationErrorCode fromValue(int value) {
        for (BGPNotificationErrorCode c : BGPNotificationErrorCode.values()) {
            if (c.value == value) {
                return c;
            }
        }
        throw new IllegalArgumentException(String.valueOf(value));
    }

    public int getValue() {
        return value;
    }
}
