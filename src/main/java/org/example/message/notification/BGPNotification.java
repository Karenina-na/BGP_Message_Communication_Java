package org.example.message.notification;

import cn.hutool.core.date.DateUtil;
import org.example.message.BGPPkt;

import java.io.IOException;

public class BGPNotification implements BGPPkt {
    private final String time;
    private final byte[] marker = new byte[16];
    private final BGPNotificationErrorCode major_error_code;
    private final BGPNotificationSubErrorCode minor_error_code;

    public BGPNotification(BGPNotificationErrorCode majorErrorCode, BGPNotificationSubErrorCode minorErrorCode) {
        major_error_code = majorErrorCode;
        minor_error_code = minorErrorCode;
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
        major error code: 1 byte
        minor error code: 1 byte
        * */
        byte[] packet = new byte[21];
        // marker  0xff * 16
        System.arraycopy(marker, 0, packet, 0, 16);
        // length   0x00 0x13
        packet[16] = (byte) ((21) >> 8);
        packet[17] = (byte) ((21) & 0xff);
        // type - keep live for 4
        packet[18] = (byte) 0x03;
        // major error code
        packet[19] = (byte) major_error_code.getValue();
        // minor error code
        packet[20] = (byte) minor_error_code.getValue();

        return packet;
    }

    @Override
    public String to_string() {
        String s = "Notification Message ====================== " + time + "\n";
        s += "Length: " + build_packet().length + "\n";
        s += "Major error code: " + major_error_code + " (" + major_error_code.getValue() + ")" + "\n";
        s += "Minor error code: " + minor_error_code + " (" + minor_error_code.getValue() + ")" + "\n";
        return s;
    }

    @Override
    public void write_to_xml(String path) throws IOException {

    }
}
