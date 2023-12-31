package org.example.message.update.path_attr;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPUpdateAttrNEXT_HOP implements BGPUpdatePathAttr{
    private final byte flags;   // default 0x40
    private int type_code;  // default 0x03
    private int length;
    private final String next_hop;   // default src ip

    public BGPUpdateAttrNEXT_HOP(byte flags, String nextHop) {
        this.flags = flags;
        this.type_code = 0x03;
        this.length = 4;
        this.next_hop = nextHop;
    }

    @Override
    public byte[] build_packet() {
        /*
        flags: 1 byte
        type code: 1 byte
        length: 1 byte
        next_hop: 4 bytes
        * */
        byte[] packet = new byte[7];
        packet[0] = flags;
        packet[1] = (byte) type_code;
        packet[2] = (byte) length;
        String[] next_hop_split = next_hop.split("\\.");
        for (int i = 0; i < 4; i++) {
            packet[3 + i] = (byte) Integer.parseInt(next_hop_split[i]);
        }
        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Attr NEXT HOP: \n";
        result += "  - flags: 0x" + Convert.toHex(new byte[] {flags}) + "\n";
        result += "  - type code: " + type_code + "\n";
        result += "  - length: " + length + "\n";
        result += "  - next hop: " + next_hop + "\n";
        return result;
    }

    @Override
    public void set_xml(Element attr) {
        Element next_hop = attr.addElement("next_hop");
        next_hop.addElement("flags").addText("0x" + Convert.toHex(new byte[] {flags})).addAttribute("size", "1");
        next_hop.addElement("type_code").addText(String.valueOf(type_code)).addAttribute("size", "1");
        next_hop.addElement("length").addText(String.valueOf(length)).addAttribute("size", "1");
        next_hop.addElement("next_hop").addText(this.next_hop).addAttribute("size", "4");
    }

    public byte getFlags() {
        return flags;
    }

    public int getType_code() {
        return type_code;
    }

    public void setType_code(int type_code) {
        this.type_code = type_code;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getNext_hop() {
        return next_hop;
    }
}
