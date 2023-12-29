package org.example.parsers.refresh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.message.refresh.BGPRefresh;

public class BGPRefreshParser {
    protected static final Logger LOGGER = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    public static BGPRefresh parse(byte[] packet) {
        /*
        marker: 16 bytes
        length: 2 bytes
        type: 1 byte
        afi: 2 bytes
        res: 1 byte
        safi: 1 byte
        */
        // type - refresh for 5
        if (packet[18] != 5) {
            LOGGER.error("Not a refresh packet");
            return null;
        }

        int afi = ((packet[19] & 0xff) << 8) | (packet[20] & 0xff);
        int safi = packet[22] & 0xff;

        return new BGPRefresh(afi, safi);
    }
}
