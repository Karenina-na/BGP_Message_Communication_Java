package org.example.message.refresh;

import cn.hutool.core.date.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.message.BGPPkt;

public class BGPRefresh implements BGPPkt {
    private final String time;
    private final byte[] marker = new byte[16];
    private final int afi;
    private final int res;
    private final int safi;
    protected static final Logger LOGGER = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);


    public BGPRefresh(int afi, int safi) {  // default res = 0, afi = 1, safi = 1
        this.afi = afi;
        this.safi = safi;
        this.res = 0;   // reserved
        for (int i = 0; i < 16; i++) {
            marker[i] = (byte) 0xff;
        }
        time = DateUtil.now();
    }

    @Override
    public byte[] build_packet() {
        /*
        marker: 16 bytes
        length: 2 bytes
        type: 1 byte
        afi: 2 bytes
        res: 1 byte
        safi: 1 byte
        * */
        byte[] packet = new byte[23];
        // marker  0xff * 16
        System.arraycopy(marker, 0, packet, 0, 16);
        // length   23
        packet[16] = (byte) 0x00;
        packet[17] = (byte) 0x17;
        // type - refresh for 5
        packet[18] = (byte) 0x05;
        // afi
        packet[19] = (byte) ((afi >> 8) & 0xff);
        packet[20] = (byte) (afi & 0xff);
        // res
        packet[21] = (byte) res;
        // safi
        packet[22] = (byte) safi;

        return packet;
    }

    @Override
    public String to_string() {
        String s = "REFRESH Message ====================== " + time + "\n";
        s += "Length: " + build_packet().length + "\n";
        s += "Address Family Identifier: " + afi + "\n";
        s += "Subsequent Address Family Identifier: " + safi + "\n";
        return s;
    }
}
