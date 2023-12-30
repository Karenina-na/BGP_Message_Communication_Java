package org.example.parsers.open;

import org.example.message.open.BGPOpen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BGPOpenParser{

    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPOpenParser.class);


    public static BGPOpen parse(byte[] packet) {
        /*
        marker: 16 bytes
        length: 2 bytes
        type: 1 byte
        version: 1 byte
        asn: 2 bytes
        holdTime: 2 bytes
        id: 4 bytes
        optLen: 1 byte
        * */
        // type - open for 1
        if (packet[18] != 1) {
            LOGGER.error("Not a open packet");
            return null;
        }

        // version  default 4
        int version = packet[19];

        // asn
        int asn = (packet[20] << 8) + (packet[21] & 0xff);

        // holdTime
        int holdTime = (packet[22] << 8) + (packet[23] & 0xff);

        // id
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            id.append((packet[24 + i] & 0xff)).append(".");
        }
        id.deleteCharAt(id.length() - 1);

        // optLen
        int optLen = packet[28];

        // optPara
        byte[] optPara = new byte[optLen];
        System.arraycopy(packet, 29, optPara, 0, optLen);

        return new BGPOpen(version, asn, holdTime, id.toString(), BGPOpenPathOptParser.parse(optPara));
    }
}
