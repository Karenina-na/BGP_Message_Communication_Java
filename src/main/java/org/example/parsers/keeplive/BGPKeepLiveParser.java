package org.example.parsers.keeplive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.message.keeplive.BGPKeepLive;

public class BGPKeepLiveParser {
    protected static final Logger LOGGER = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    public static BGPKeepLive parse(byte[] packet) {
        /*
        marker: 16 bytes
        length: 2 bytes
        type: 1 byte
        * */
        // type - keep live for 4
        if (packet[18] != 4) {
            LOGGER.error("Not a keep live message.");
            return null;
        }

        return new BGPKeepLive();
    }
}
