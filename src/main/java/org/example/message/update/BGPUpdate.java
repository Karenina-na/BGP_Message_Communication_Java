package org.example.message.update;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.example.message.BGPPkt;
import org.example.message.update.path_attr.BGPUpdatePathAttr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Vector;

public class BGPUpdate implements BGPPkt {
    private final String time;
    private final byte[] marker = new byte[16];
    private final boolean isWithdrawn;
    Vector<BGPUpdatePathAttr> pathAttr;
    Vector<BGPUpdateNLRI> nlri;

    public BGPUpdate(boolean isWithdrawn, Vector<BGPUpdatePathAttr> pathAttr, Vector<BGPUpdateNLRI> nlri) {
        // if withdrawn, the nlri is withdrawn routes
        this.pathAttr = pathAttr == null ? new Vector<>() : pathAttr;
        this.nlri = nlri == null ? new Vector<>() : nlri;
        this.isWithdrawn = isWithdrawn;
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
        withdrawnLen: 2 bytes
        pathAttrLen: 2 bytes
        pathAttr: pathAttrLen bytes
        nlri: nlriLen bytes
        * */
        int pathAttrLen = 0;
        for (BGPUpdatePathAttr attr : pathAttr) {
            pathAttrLen += attr.build_packet().length;
        }
        int nlriLen = 0;
        for (BGPUpdateNLRI nlri : nlri) {
            nlriLen += nlri.build_packet().length;
        }
        int length;
        int withdrawnLen = 0;
        if (isWithdrawn) {
            // 计算withdrawnLen
            for (BGPUpdateNLRI nlri : nlri) {
                withdrawnLen += nlri.build_packet().length;
            }
            length = 23 + withdrawnLen + pathAttrLen;
        } else {
            length = 23 + nlriLen + pathAttrLen;
        }
        byte[] packet = new byte[length];

        // marker  0xff * 16
        System.arraycopy(marker, 0, packet, 0, 16);
        // length   0x00 0x1d + withdrawnLen + pathAttrLen + nlriLen
        packet[16] = (byte) (length >> 8);
        packet[17] = (byte) (length & 0xff);
        // type - update for 2
        packet[18] = (byte) 0x02;
        // withdrawnLen
        packet[19] = (byte) (withdrawnLen >> 8);
        packet[20] = (byte) (withdrawnLen & 0xff);

        if (isWithdrawn) {
            // nlri - withdrawn
            int point = 21;
            while (point < withdrawnLen + 21) {
                for (BGPUpdateNLRI nlri : nlri) {
                    System.arraycopy(nlri.build_packet(), 0, packet, point, nlri.build_packet().length);
                    point += nlri.build_packet().length;
                }
            }
        }
        // pathAttrLen
        packet[21 + withdrawnLen] = (byte) (pathAttrLen >> 8);
        packet[22 + withdrawnLen] = (byte) (pathAttrLen & 0xff);
        // pathAttr
        int point = 23 + withdrawnLen;
        while (point < pathAttrLen + 23) {
            for (BGPUpdatePathAttr attr : pathAttr) {
                System.arraycopy(attr.build_packet(), 0, packet, point, attr.build_packet().length);
                point += attr.build_packet().length;
            }
        }

        if (isWithdrawn) {
            return packet;
        }
        // nlri
        while (point < nlriLen + 23 + pathAttrLen) {
            for (BGPUpdateNLRI nlri : nlri) {
                System.arraycopy(nlri.build_packet(), 0, packet, point, nlri.build_packet().length);
                point += nlri.build_packet().length;
            }
        }
        return packet;
    }

    @Override
    public String to_string() {
        String s = "UPDATE Message ====================== " + time + "\n";
        s += "Length: " + build_packet().length + "\n";
        if (isWithdrawn) {
            s += "Withdrawn Routes Length: " + nlri.size() + "\n";
            s += "Withdrawn Routes: \n";
            StringBuilder sBuilder = new StringBuilder(s);
            for (BGPUpdateNLRI nlri : nlri) {
                sBuilder.append(nlri.to_string());
            }
            s = sBuilder.toString();
        } else {
            s += "Withdrawn Routes Length: " + 0 + "\n";
        }
        int pathAttrLen = 0;
        for (BGPUpdatePathAttr attr : pathAttr) {
            pathAttrLen += attr.build_packet().length;
        }
        s += "Total Path Attributes Length: " + pathAttrLen + "\n";
        s += "Path Attributes: \n";
        StringBuilder sBuilder = new StringBuilder(s);
        for (BGPUpdatePathAttr attr : pathAttr) {
            sBuilder.append(attr.to_string());
        }
        s = sBuilder.toString();

        if (isWithdrawn) {
            return s;
        }
        s += "Network Layer Reachability Information: \n";
        sBuilder = new StringBuilder(s);
        for (BGPUpdateNLRI nlri : nlri) {
            sBuilder.append(nlri.to_string());
        }
        s = sBuilder.toString();
        return s;
    }

    @Override
    public void write_to_xml(String path_relative) throws IOException {
        // root
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("bgp_update");
        // header
        Element header = root.addElement("header");
        header.addElement("marker").addText(Convert.toHex(this.marker)).addAttribute("size", "16");
        header.addElement("length").addText(String.valueOf(build_packet().length)).addAttribute("size", "2");
        header.addElement("type").addText("2").addAttribute("size", "1");
        // body
        Element body = root.addElement("body");
        if (isWithdrawn) {
            body.addElement("withdrawn_len").addText(String.valueOf(nlri.size())).addAttribute("size", "2");
            Element withdrawn = body.addElement("withdrawn_routes");
            for (BGPUpdateNLRI nlri : nlri) {
                Element nlriEle = withdrawn.addElement("withdrawn_route");
                nlri.set_xml(nlriEle);
            }
        } else {
            body.addElement("withdrawn_len").addText(String.valueOf(0)).addAttribute("size", "2");
        }
        int pathAttrLen = 0;
        for (BGPUpdatePathAttr attr : pathAttr) {
            pathAttrLen += attr.build_packet().length;
        }
        body.addElement("total_path_attr_len").addText(String.valueOf(pathAttrLen)).addAttribute("size", "2");
        Element pathAttr = body.addElement("path_attr");
        for (BGPUpdatePathAttr attr : this.pathAttr) {
            attr.set_xml(pathAttr);
        }
        if (isWithdrawn) {
            return;
        }
        Element nlri = body.addElement("nlri");
        for (BGPUpdateNLRI nlriEle : this.nlri) {
            nlriEle.set_xml(nlri);
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
}
