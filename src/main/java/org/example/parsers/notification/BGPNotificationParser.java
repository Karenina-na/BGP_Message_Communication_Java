package org.example.parsers.notification;

import org.example.message.notification.BGPNotification;
import org.example.message.notification.BGPNotificationErrorCode;
import org.example.message.notification.BGPNotificationSubErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BGPNotificationParser {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPNotificationParser.class);
    public static BGPNotification parse(byte[] packet) {
        /*
        marker: 16 bytes
        length: 2 bytes
        type: 1 byte
        major error code: 1 byte
        minor error code: 1 byte
        * */
        // type - notification for 3
        if (packet[18] != 3) {
            LOGGER.error("Not a notification packet");
            return null;
        }

        return new BGPNotification(BGPNotificationErrorCode.fromValue(packet[19]),
                BGPNotificationSubErrorCode.fromValue(BGPNotificationErrorCode.fromValue(packet[19]), packet[20]));
    }
}
