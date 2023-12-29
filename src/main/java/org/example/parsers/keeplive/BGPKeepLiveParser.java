package org.example.parsers.keeplive;

import org.example.BGPClient;
import org.example.message.keeplive.BGPKeepLive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BGPKeepLiveParser {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPClient.class);
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
