package org.example.message.update.path_attr;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPUpdateAttrNEXT_HOP implements BGPUpdatePathAttr{
    private final byte flags;   // default 0x40
    private final String next_hop;   // default src ip

    public BGPUpdateAttrNEXT_HOP(byte flags, String nextHop) {
        this.flags = flags;
        next_hop = nextHop;
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
        packet[1] = (byte) 0x03;
        packet[2] = (byte) 0x04;
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
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) 0x04}) + "\n";
        result += "  - next hop: " + next_hop + "\n";
        return result;
    }

    @Override
    public void set_xml(Element attr) {
        Element next_hop = attr.addElement("next_hop");
        next_hop.addElement("flags").addText("0x" + Convert.toHex(new byte[] {flags})).addAttribute("size", "1");
        next_hop.addElement("type_code").addText(String.valueOf(3)).addAttribute("size", "1");
        next_hop.addElement("length").addText(String.valueOf(4)).addAttribute("size", "1");
        next_hop.addElement("next_hop").addText(this.next_hop).addAttribute("size", "4");
    }
}
