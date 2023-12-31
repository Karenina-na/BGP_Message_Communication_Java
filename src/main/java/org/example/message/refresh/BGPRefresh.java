package org.example.message.refresh;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.example.BGPClient;
import org.example.message.BGPPkt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class BGPRefresh implements BGPPkt {
    private final String time;
    private byte[] marker = new byte[16];
    private int length;

    private int type;
    private int afi;
    private int res;
    private int safi;
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPClient.class);


    public BGPRefresh(int afi, int safi) {  // default res = 0, afi = 1, safi = 1
        this.length = 23;
        this.type = 5;
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
        packet[16] = (byte) (length >> 8);
        packet[17] = (byte) (length & 0xff);
        // type - refresh for 5
        packet[18] = (byte) type;
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
        s += "Length: " + length + "\n";
        s += "Address Family Identifier: " + afi + "\n";
        s += "Subsequent Address Family Identifier: " + safi + "\n";
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
        body.addElement("afi").addText(String.valueOf(afi)).addAttribute("size", "2");
        body.addElement("res").addText(String.valueOf(res)).addAttribute("size", "1");
        body.addElement("safi").addText(String.valueOf(safi)).addAttribute("size", "1");

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

    public void setMarker(byte[] marker) {
        this.marker = marker;
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

    public int getAfi() {
        return afi;
    }

    public void setAfi(int afi) {
        this.afi = afi;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getSafi() {
        return safi;
    }

    public void setSafi(int safi) {
        this.safi = safi;
    }
}
