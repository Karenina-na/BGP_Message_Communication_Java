package org.example.parsers.update;

import org.example.BGPClient;
import org.example.message.update.BGPUpdate;
import org.example.message.update.BGPUpdateNLRI;
import org.example.message.update.path_attr.BGPUpdatePathAttr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class BGPUpdateParser{

    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPClient.class);

    public static BGPUpdate parse(byte[] pkt) {
        /*
        marker: 16 bytes
        length: 2 bytes
        type: 1 byte
        withdrawnLen: 2 bytes
        pathAttrLen: 2 bytes
        pathAttr: pathAttrLen bytes
        nlri: nlriLen bytes
        * */
        // type - update for 2
        if (pkt[18] != 2) {
            LOGGER.error("Not a update packet");
            return null;
        }

        // withdrawnLen
        int withdrawnLen = (pkt[19] << 8) + (pkt[20] & 0xff);

        // withdrawn - nlri
        Vector<BGPUpdateNLRI> nlris = null;
        if (withdrawnLen != 0) {
            nlris = new Vector<>();
            int point = 21;
            while (point < 21 + withdrawnLen) {
                int nlriLen = pkt[point] & 0xff;
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < nlriLen / 8; i++) {
                    prefix.append(pkt[point + 1 + i] & 0xff);
                    prefix.append(".");
                }
                prefix.append("0.".repeat(Math.max(0, 4 - nlriLen / 8))).deleteCharAt(prefix.length() - 1);
                nlris.add(new BGPUpdateNLRI(nlriLen, prefix.toString()));
                point += 1 + nlriLen / 8;
            }
        }

        // pathAttrLen
        int pathAttrLen = (pkt[21 + withdrawnLen] << 8) + (pkt[22 + withdrawnLen] & 0xff);

        // pathAttr
        byte[] pathAttr = new byte[pathAttrLen];
        System.arraycopy(pkt, 23 + withdrawnLen, pathAttr, 0, pathAttrLen);
        Vector<BGPUpdatePathAttr> pathAttrs = BGPUpdatePathAttrParser.parse(pathAttr);

        if (withdrawnLen != 0) {
            return new BGPUpdate(true, pathAttrs, nlris);
        }

        // nlri
        nlris = new Vector<>();
        int point = 23 + pathAttrLen;
        while (point < pkt.length) {
            int nlriLen = pkt[point] & 0xff;
            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < nlriLen / 8; i++) {
                prefix.append(pkt[point + 1 + i] & 0xff);
                prefix.append(".");
            }
            prefix.append("0.".repeat(Math.max(0, 4 - nlriLen / 8))).deleteCharAt(prefix.length() - 1);
            nlris.add(new BGPUpdateNLRI(nlriLen, prefix.toString()));
            point += 1 + nlriLen / 8;
        }

        return new BGPUpdate(false, pathAttrs, nlris);
    }
}
