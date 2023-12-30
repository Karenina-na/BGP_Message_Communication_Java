package org.example.message.open.open_opt;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPOpenOptMultiprotocolExtCap implements BGPOpenOpt{

    private final int afi;
    private final int safi;

    public BGPOpenOptMultiprotocolExtCap(int afi, int safi) {
        this.afi = afi;
        this.safi = safi;
    }

    @Override
    public byte[] build_packet() {
        /*
        type code: 1 byte   2
        length: 1 byte  6
        type: 1 byte    1
        length: 1 byte  4
        afi: 2 bytes    1
        reserved: 1 byte    0
        safi: 1 byte    1
        * */
        byte[] packet = new byte[8];
        // type code
        packet[0] = (byte) 0x02;
        // length
        packet[1] = (byte) 0x06;
        // type
        packet[2] = (byte) 0x01;
        // length
        packet[3] = (byte) 0x04;
        // afi
        packet[4] = (byte) (afi >> 8);
        packet[5] = (byte) (afi & 0xff);
        // reserved
        packet[6] = (byte) 0x00;
        // safi
        packet[7] = (byte) safi;

        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Opt Param: Capability (" + 0x02 + ")\n";
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) 0x06}) + "\n";
        result += "  - capability: Multiprotocol Extensions (" + 0x01 + ")\n";
        result += "    - afi: 0x" + Convert.toHex(new byte[] {(byte) (afi >> 8), (byte) (afi & 0xff)}) + "\n";
        result += "    - reserved: 0x" + Convert.toHex(new byte[] {(byte) 0x00}) + "\n";
        result += "    - safi: 0x" + Convert.toHex(new byte[] {(byte) safi}) + "\n";
        return result;
    }

    @Override
    public void set_xml(Element opt) {
        // opt - Capability
        Element capability = opt.addElement("Capability");
        capability.addElement("type").addText("2").addAttribute("size", "1");
        capability.addElement("length").addText("6").addAttribute("size", "1");
        // capability - Multiprotocol Extensions
        Element multiExt = capability.addElement("MultiProtocolExtensions");
        multiExt.addElement("type").addText("1").addAttribute("size", "1");
        multiExt.addElement("length").addText("4").addAttribute("size", "1");
        multiExt.addElement("afi").addText(String.valueOf(afi)).addAttribute("size", "2");
        multiExt.addElement("reserved").addText("00").addAttribute("size", "1");
        multiExt.addElement("safi").addText(String.valueOf(safi)).addAttribute("size", "1");
    }
}
