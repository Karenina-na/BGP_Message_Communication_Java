package org.example.message.keeplive;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import org.example.message.BGPPkt;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    @Override
    public void write_to_xml(String path_relative) throws IOException {
        // root
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("bgp_keeplive");
        // header
        Element header = root.addElement("header");
        header.addElement("marker").addText(Convert.toHex(this.marker)).addAttribute("size", "16");
        header.addElement("length").addText(String.valueOf(build_packet().length)).addAttribute("size", "2");
        header.addElement("type").addText("4").addAttribute("size", "1");
        // body - none

        // write to file - resources
//        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath() + path_relative;
        XMLWriter writer = new XMLWriter(
                new OutputStreamWriter(new FileOutputStream(path_relative), StandardCharsets.UTF_8),
                OutputFormat.createPrettyPrint()
        );
        writer.write(document);
        writer.close();
    }

}
