package org.example.message.update.path_attr;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPUpdateAttrORIGIN implements BGPUpdatePathAttr{

    private final byte flags;   // default 0x40
    private final int origin;   // 0: IGP, 1: EGP, 2: INCOMPLETE

    public BGPUpdateAttrORIGIN(byte flags, int origin) {
        this.flags = flags;
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
        packet[1] = (byte) 0x01;
        packet[2] = (byte) 0x01;
        packet[3] = (byte) origin;
        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Attr ORIGIN: \n";
        result += "  - flags: 0x" + Convert.toHex(new byte[] {flags}) + "\n";
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) 0x01}) + "\n";
        result += "  - origin: 0x" + Convert.toHex(new byte[] {(byte) origin}) + "\n";
        return result;
    }

    @Override
    public void set_xml(Element attr) {
        Element origin = attr.addElement("origin");
        origin.addElement("flags").addText("0x" + Convert.toHex(new byte[] {flags})).addAttribute("size", "1");
        origin.addElement("type_code").addText(String.valueOf(1)).addAttribute("size", "1");
        origin.addElement("length").addText(String.valueOf(1)).addAttribute("size", "1");
        origin.addElement("origin").addText(String.valueOf(this.origin)).addAttribute("size", "1");
    }
}
