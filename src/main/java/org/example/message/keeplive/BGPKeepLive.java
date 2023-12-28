package org.example.message.keeplive;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import org.example.message.BGPPkt;

public class BGPKeepLive implements BGPPkt {
    private final String time;

    private final byte[] marker = new byte[16];

    public BGPKeepLive() {
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
        * */
        byte[] packet = new byte[19];
        // marker  0xff * 16
        System.arraycopy(marker, 0, packet, 0, 16);
        // length   0x00 0x13
        packet[16] = (byte) ((19) >> 8);
        packet[17] = (byte) ((19) & 0xff);
        // type - keep live for 4
        packet[18] = (byte) 0x04;

        return packet;
    }

    @Override
    public String to_string() {
        String s = "KeepLive Message ====================== " + time + "\n";
        s += "Length: " + build_packet().length + "\n";
        return s;
    }
}
