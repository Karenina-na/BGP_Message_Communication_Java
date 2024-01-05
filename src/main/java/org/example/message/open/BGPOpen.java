package org.example.message.open;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.example.BGPClient;
import org.example.message.BGPPkt;
import org.example.message.open.open_opt.BGPOpenOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class BGPOpen implements BGPPkt {
    private final String time;
    private byte[] marker = new byte[16];
    private int length;

    private int type;
    private int version;
    private int asn;
    private int holdTime;
    private String id;
    private int optLen;
    private Vector<BGPOpenOpt> optPara;
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPOpen.class);

    public BGPOpen(int version, int asn, int holdTime, String id, Vector<BGPOpenOpt> optPara) {
        for (int i = 0; i < 16; i++) {
            marker[i] = (byte) 0xff;
        }
        int optLen = 0;
        if (optPara != null) {
            for (BGPOpenOpt opt : optPara) {
                optLen += opt.build_packet().length;
            }
        }
        this.optLen = optLen;
        this.length = 29 + optLen;
        this.type = 1;
        this.version = version;
        this.asn = asn;
        this.holdTime = holdTime;
        this.id = id;
        this.optPara = optPara;
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
        byte[] packet = new byte[29 + optLen];
        // marker  0xff * 16
        System.arraycopy(marker, 0, packet, 0, 16);
        // length   0x00 0x1d + optLen
        packet[16] = (byte) (length >> 8);
        packet[17] = (byte) (length & 0xff);
        // type - open for 1
        packet[18] = (byte) type;
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

    @Override
    public void write_to_xml(String path_relative) throws IOException {
        // root
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("bgp_open");
        // header
        Element header = root.addElement("header");
        header.addElement("marker").addText(Convert.toHex(this.marker)).addAttribute("size", "16");
        header.addElement("length").addText(String.valueOf(this.length)).addAttribute("size", "2");
        header.addElement("type").addText(String.valueOf(this.type)).addAttribute("size", "1");
        // body
        Element body = root.addElement("body");
        body.addElement("version").addText(String.valueOf(version)).addAttribute("size", "1");
        body.addElement("asn").addText(String.valueOf(asn)).addAttribute("size", "2");
        body.addElement("hold_time").addText(String.valueOf(holdTime)).addAttribute("size", "2");
        body.addElement("id").addText(id).addAttribute("size", "4");
        body.addElement("opt_len").addText(String.valueOf(optLen)).addAttribute("size", "1");
        Element opt = body.addElement("opt");
        if (optLen != 0) {
            for (BGPOpenOpt optPara : optPara) {
                optPara.set_xml(opt);
            }
        }

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getAsn() {
        return asn;
    }

    public void setAsn(int asn) {
        this.asn = asn;
    }

    public int getHoldTime() {
        return holdTime;
    }

    public void setHoldTime(int holdTime) {
        this.holdTime = holdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOptLen() {
        return optLen;
    }

    public void setOptLen(int optLen) {
        this.optLen = optLen;
    }

    public Vector<BGPOpenOpt> getOptPara() {
        return optPara;
    }

    public void setOptPara(Vector<BGPOpenOpt> optPara) {
        this.optPara = optPara;
    }
}
