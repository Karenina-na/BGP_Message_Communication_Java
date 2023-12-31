package org.example.message.update.path_attr;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPUpdateAttrORIGIN implements BGPUpdatePathAttr{

    private final byte flags;   // default 0x40
    private int type_code;  // default 0x01
    private int length;
    private final int origin;   // 0: IGP, 1: EGP, 2: INCOMPLETE

    public BGPUpdateAttrORIGIN(byte flags, int origin) {
        this.flags = flags;
        this.type_code = 0x01;
        this.length = 1;
        this.origin = origin;
    }
    @Override
    public byte[] build_packet() {
        /*
        flags: 1 byte
        type code: 1 byte
        length: 1 byte
        origin: 1 byte
        * */
        byte[] packet = new byte[4];
        packet[0] = flags;
        packet[1] = (byte) type_code;
        packet[2] = (byte) length;
        packet[3] = (byte) origin;
        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Attr ORIGIN: \n";
        result += "  - flags: 0x" + Convert.toHex(new byte[] {flags}) + "\n";
        result += "  - type code: " + type_code + "\n";
        result += "  - length: " + length + "\n";
        result += "  - origin: " + origin + "\n";
        return result;
    }

    @Override
    public void set_xml(Element attr) {
        Element origin = attr.addElement("origin");
        origin.addElement("flags").addText("0x" + Convert.toHex(new byte[] {flags})).addAttribute("size", "1");
        origin.addElement("type_code").addText(String.valueOf(type_code)).addAttribute("size", "1");
        origin.addElement("length").addText(String.valueOf(length)).addAttribute("size", "1");
        origin.addElement("origin").addText(String.valueOf(this.origin)).addAttribute("size", "1");
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

    public int getOrigin() {
        return origin;
    }
}
