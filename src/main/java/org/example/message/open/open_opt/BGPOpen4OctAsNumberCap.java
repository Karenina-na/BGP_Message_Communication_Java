package org.example.message.open.open_opt;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPOpen4OctAsNumberCap implements BGPOpenOpt{

    private final int asn;

    public BGPOpen4OctAsNumberCap(int asn) {
        this.asn = asn;
    }
    @Override
    public byte[] build_packet() {
        /*
        type code: 1 byte
        length: 1 byte
        type: 1 byte
        length: 1 byte
        asn: 4 bytes
        * */
        byte[] packet = new byte[8];
        // type code
        packet[0] = (byte) 0x02;
        // length
        packet[1] = (byte) 0x06;
        // type
        packet[2] = (byte) 0x41;
        // length
        packet[3] = (byte) 0x04;
        // asn
        packet[4] = (byte) (asn >> 24);
        packet[5] = (byte) ((asn >> 16) & 0xff);
        packet[6] = (byte) ((asn >> 8) & 0xff);
        packet[7] = (byte) (asn & 0xff);

        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Opt Param: Capability (" + 0x02 + ")\n";
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) 0x06}) + "\n";
        result += "  - capability: 4-octet AS number (" + 0x41 + ")\n";
        result += "    - asn: " + asn + "\n";
        return result;
    }

    @Override
    public void set_xml(Element opt) {
        // opt - Capability
        Element capability = opt.addElement("Capability");
        capability.addElement("type").addText("2").addAttribute("size", "1");
        capability.addElement("length").addText("6").addAttribute("size", "1");
        // capability - 4-octet AS number
        Element four_octet_as_number = capability.addElement("FourOctetASNumber");
        four_octet_as_number.addElement("type").addText("65").addAttribute("size", "1");
        four_octet_as_number.addElement("length").addText("4").addAttribute("size", "1");
        four_octet_as_number.addElement("asn").addText(String.valueOf(asn)).addAttribute("size", "4");
    }
}
