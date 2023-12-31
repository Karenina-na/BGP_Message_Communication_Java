package org.example.message.notification;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.example.message.BGPPkt;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class BGPNotification implements BGPPkt {
    private final String time;
    private final byte[] marker = new byte[16];
    private int length;

    private int type;
    private final BGPNotificationErrorCode major_error_code;
    private final BGPNotificationSubErrorCode minor_error_code;

    public BGPNotification(BGPNotificationErrorCode majorErrorCode, BGPNotificationSubErrorCode minorErrorCode) {
        major_error_code = majorErrorCode;
        minor_error_code = minorErrorCode;
        length = 21;
        type = 3;
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
        packet[16] = (byte) (length >> 8);
        packet[17] = (byte) (length & 0xff);
        // type - notification for 3
        packet[18] = (byte) type;
        // major error code
        packet[19] = (byte) major_error_code.getValue();
        // minor error code
        packet[20] = (byte) minor_error_code.getValue();

        return packet;
    }

    @Override
    public String to_string() {
        String s = "Notification Message ====================== " + time + "\n";
        s += "Length: " + length + "\n";
        s += "Major error code: " + major_error_code + " (" + major_error_code.getValue() + ")" + "\n";
        s += "Minor error code: " + minor_error_code + " (" + minor_error_code.getValue() + ")" + "\n";
        return s;
    }

    @Override
    public void write_to_xml(String path_relative) throws IOException {
        // root
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("bgp_notification");
        // header
        Element header = root.addElement("header");
        header.addElement("marker").addText(Convert.toHex(this.marker)).addAttribute("size", "16");
        header.addElement("length").addText(String.valueOf(length)).addAttribute("size", "2");
        header.addElement("type").addText(String.valueOf(type)).addAttribute("size", "1");
        // body
        Element body = root.addElement("body");
        body.addElement("major_error_code").addText(String.valueOf(major_error_code.getValue())).addAttribute("size", "1");
        body.addElement("minor_error_code").addText(String.valueOf(minor_error_code.getValue())).addAttribute("size", "1");

        // write to file - resources
//        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath() + path_relative;
        XMLWriter writer = new XMLWriter(
                new OutputStreamWriter(new FileOutputStream(path_relative), StandardCharsets.UTF_8),
                OutputFormat.createPrettyPrint()
        );
        writer.write(document);
        writer.close();
    }

    public byte[] getMarker() {
        return marker;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BGPNotificationErrorCode getMajor_error_code() {
        return major_error_code;
    }

    public BGPNotificationSubErrorCode getMinor_error_code() {
        return minor_error_code;
    }
}
