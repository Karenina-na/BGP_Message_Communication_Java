package org.example.message.open;

import cn.hutool.core.date.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.message.BGPPkt;
import org.example.message.open.open_opt.BGPOpenOpt;

import java.util.Vector;

public class BGPOpen implements BGPPkt {
    private final String time;
    private final byte[] marker = new byte[16];
    private final int version;
    private final int asn;
    private final int holdTime;
    private final String id;
    private final Vector<BGPOpenOpt> optPara;
    protected static final Logger LOGGER = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public BGPOpen(int version, int asn, int holdTime, String id, Vector<BGPOpenOpt> optPara) {
        this.version = version;
        this.asn = asn;
        this.holdTime = holdTime;
        this.id = id;
        this.optPara = optPara;
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
        version: 1 byte
        asn: 2 bytes
        holdTime: 2 bytes
        id: 4 bytes
        optLen: 1 byte
        * */
        int optLen = 0;
        if (optPara != null) {
            for (BGPOpenOpt opt : optPara) {
                optLen += opt.build_packet().length;
            }
        }
        byte[] packet = new byte[29 + optLen];
        // marker  0xff * 16
        System.arraycopy(marker, 0, packet, 0, 16);
        // length   0x00 0x1d + optLen
        packet[16] = (byte) ((29 + optLen) >> 8);
        packet[17] = (byte) ((29 + optLen) & 0xff);
        // type - open for 1
        packet[18] = (byte) 0x01;
        // version  default 4
        packet[19] = (byte) version;
        // asn
        packet[20] = (byte) (asn >> 8);
        packet[21] = (byte) (asn & 0xff);
        // holdTime
        packet[22] = (byte) (holdTime >> 8);
        packet[23] = (byte) (holdTime & 0xff);
        // id
        String[] idStr = id.split("\\.");
        for (int i = 0; i < 4; i++) {
            packet[24 + i] = (byte) Integer.parseInt(idStr[i]);
        }
        // optLen
        packet[28] = (byte) optLen;
        // optPara
        if (optLen != 0) {
            int offset = 29;
            for (BGPOpenOpt opt : optPara) {
                byte[] optBytes = opt.build_packet();
                System.arraycopy(optBytes, 0, packet, offset, optBytes.length);
                offset += optBytes.length;
            }
        }
        return packet;
    }

    @Override
    public String to_string() {
        String s = "OPEN Message ====================== " + time + "\n";
        s += "Length: " + build_packet().length + "\n";
        s += "Version: " + version + "\n";
        s += "ASN: " + asn + "\n";
        s += "Hold Time: " + holdTime + "\n";
        s += "ID: " + id + "\n";
        int optLen = 0;
        if (optPara != null) {
            for (BGPOpenOpt opt : optPara) {
                optLen += opt.build_packet().length;
            }
        }
        s += "Optional Parameters Length Len: " + optLen + "\n";
        s += "Optional Parameters: \n";
        if (optLen != 0) {
            StringBuilder sBuilder = new StringBuilder(s);
            for (BGPOpenOpt opt : optPara) {
                sBuilder.append(opt.to_string());
            }
            s = sBuilder.toString();
        }
        return s;
    }
}
