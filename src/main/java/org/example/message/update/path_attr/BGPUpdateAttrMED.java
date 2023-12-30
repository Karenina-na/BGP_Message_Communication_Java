package org.example.message.update.path_attr;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPUpdateAttrMED implements BGPUpdatePathAttr{

    private final byte flags;   // default 0x80
    private final int med;   // default 0

    public BGPUpdateAttrMED(byte flags, int med) {
        this.flags = flags;
        this.med = med;
    }

    @Override
    public byte[] build_packet() {
        /*
        flags: 1 byte
        type code: 1 byte
        length: 1 byte
        med: 4 bytes
        * */
        byte[] packet = new byte[7];
        packet[0] = flags;
        packet[1] = (byte) 0x04;
        packet[2] = (byte) 0x04;
        packet[3] = (byte) (med >> 24); // 32 bit med value
        packet[4] = (byte) ((med >> 16) & 0xff);
        packet[5] = (byte) ((med >> 8) & 0xff);
        packet[6] = (byte) (med & 0xff);
        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Attr MULTI EXIT DISC: \n";
        result += "  - flags: 0x" + Convert.toHex(new byte[] {flags}) + "\n";
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) 0x04}) + "\n";
        result += "  - Multiple exit discriminator: " + med + "\n";
        return result;
    }

    @Override
    public void set_xml(Element attr) {
        Element med = attr.addElement("med");
        med.addElement("flags").addText("0x" + Convert.toHex(new byte[] {flags})).addAttribute("size", "1");
        med.addElement("type_code").addText(String.valueOf(4)).addAttribute("size", "1");
        med.addElement("length").addText(String.valueOf(4)).addAttribute("size", "1");
        med.addElement("med").addText(String.valueOf(this.med)).addAttribute("size", "4");
    }
}
