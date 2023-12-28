package org.example.parsers.notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.message.keeplive.BGPKeepLive;
import org.example.message.notification.BGPNotification;

public class BGPNotificationParser {
    protected static final Logger LOGGER = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
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

        return new BGPNotification(packet[19], packet[20]);
    }
}
