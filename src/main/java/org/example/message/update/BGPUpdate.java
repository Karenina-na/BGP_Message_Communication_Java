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
import java.util.Vector;

public class BGPUpdate implements BGPPkt {
    private final String time;
    private final byte[] marker = new byte[16];
    private int length;

    private int type;
    private final boolean isWithdrawn;
    private int withdrawnLen;
    private int pathAttrLen;
    Vector<BGPUpdatePathAttr> pathAttr;
    private int nlriLen;
    Vector<BGPUpdateNLRI> nlri;

    public BGPUpdate(boolean isWithdrawn, Vector<BGPUpdatePathAttr> pathAttr, Vector<BGPUpdateNLRI> nlris) {
        // if withdrawn, the nlri is withdrawn routes
        this.type = 2;
        this.pathAttr = pathAttr == null ? new Vector<>() : pathAttr;
        this.nlri = nlris == null ? new Vector<>() : nlris;
        this.isWithdrawn = isWithdrawn;
        for (int i = 0; i < 16; i++) {
            marker[i] = (byte) 0xff;
        }
        pathAttrLen = 0;
        assert pathAttr != null;
        for (BGPUpdatePathAttr attr : pathAttr) {
            pathAttrLen += attr.build_packet().length;
        }
        nlriLen = 0;
        assert nlris != null;
        for (BGPUpdateNLRI nlri : nlris) {
            nlriLen += nlri.build_packet().length;
        }
        withdrawnLen = 0;
        if (isWithdrawn) {
            // 计算withdrawnLen
            for (BGPUpdateNLRI nlri : nlris) {
                withdrawnLen += nlri.build_packet().length;
            }
            length = 23 + withdrawnLen + pathAttrLen;
        } else {
            length = 23 + nlriLen + pathAttrLen;
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

        byte[] packet = new byte[length];

        // marker  0xff * 16
        System.arraycopy(marker, 0, packet, 0, 16);
        // length   0x00 0x1d + withdrawnLen + pathAttrLen + nlriLen
        packet[16] = (byte) (length >> 8);
        packet[17] = (byte) (length & 0xff);
        // type - update for 2
        packet[18] = (byte) type;
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
        s += "Withdrawn Routes Length: " + withdrawnLen + "\n";
        if (isWithdrawn) {
            s += "Withdrawn Routes: \n";
            StringBuilder sBuilder = new StringBuilder(s);
            for (BGPUpdateNLRI nlri : nlri) {
                sBuilder.append(nlri.to_string());
            }
            s = sBuilder.toString();
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
        header.addElement("marker").addText(Convert.toHex(marker)).addAttribute("size", "16");
        header.addElement("length").addText(String.valueOf(length)).addAttribute("size", "2");
        header.addElement("type").addText(String.valueOf(type)).addAttribute("size", "1");
        // body
        Element body = root.addElement("body");
        body.addElement("withdrawn_len").addText(String.valueOf(withdrawnLen)).addAttribute("size", "2");
        if (isWithdrawn) {
            Element withdrawn = body.addElement("withdrawn_routes");
            for (BGPUpdateNLRI nlri : nlri) {
                Element nlriEle = withdrawn.addElement("withdrawn_route");
                nlri.set_xml(nlriEle);
            }
        }
        body.addElement("total_path_attr_len").addText(String.valueOf(pathAttrLen)).addAttribute("size", "2");
        Element pathAttr = body.addElement("path_attr");
        for (BGPUpdatePathAttr attr : this.pathAttr) {
            attr.set_xml(pathAttr);
        }
        if (!isWithdrawn) {
            Element nlris = body.addElement("nlris");
            for (BGPUpdateNLRI nlriEle : this.nlri) {
                Element nlri = nlris.addElement("nlri");
                nlriEle.set_xml(nlri);
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

    public int getWithdrawnLen() {
        return withdrawnLen;
    }

    public void setWithdrawnLen(int withdrawnLen) {
        this.withdrawnLen = withdrawnLen;
    }

    public int getPathAttrLen() {
        return pathAttrLen;
    }

    public void setPathAttrLen(int pathAttrLen) {
        this.pathAttrLen = pathAttrLen;
    }

    public Vector<BGPUpdatePathAttr> getPathAttr() {
        return pathAttr;
    }

    public void setPathAttr(Vector<BGPUpdatePathAttr> pathAttr) {
        this.pathAttr = pathAttr;
    }

    public Vector<BGPUpdateNLRI> getNlri() {
        return nlri;
    }

    public void setNlri(Vector<BGPUpdateNLRI> nlri) {
        this.nlri = nlri;
    }
}
