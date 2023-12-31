package org.example.message.open.open_opt;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPOpenOptMultiprotocolExtCap extends BGPOpenOptAbc implements BGPOpenOpt {

    private int afi;
    private int reserved;
    private int safi;

    public BGPOpenOptMultiprotocolExtCap(int afi, int safi) {
        this.paramType = 2;
        this.paramLen = 6;
        this.capabilityCode = 1;
        this.capabilityLen = 4;
        this.afi = afi;
        this.reserved = 0;
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
        packet[0] = (byte) this.paramType;
        // length
        packet[1] = (byte) this.paramLen;
        // type
        packet[2] = (byte) this.capabilityCode;
        // length
        packet[3] = (byte) this.capabilityLen;
        // afi
        packet[4] = (byte) (afi >> 8);
        packet[5] = (byte) (afi & 0xff);
        // reserved
        packet[6] = (byte) reserved;
        // safi
        packet[7] = (byte) safi;

        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Opt Param: Capability (" + paramType + ")\n";
        result += "  - length: " + paramLen + "\n";
        result += "  - capability: Multiprotocol Extensions (" + capabilityCode + ")\n";
        result += "    - length: " + capabilityLen + "\n";
        result += "    - afi: 0x" + Convert.toHex(new byte[] {(byte) (afi >> 8), (byte) (afi & 0xff)}) + "\n";
        result += "    - reserved: 0x" + Convert.toHex(new byte[] {(byte) this.reserved}) + "\n";
        result += "    - safi: 0x" + Convert.toHex(new byte[] {(byte) safi}) + "\n";
        return result;
    }

    @Override
    public void set_xml(Element opt) {
        // opt - Capability
        Element capability = opt.addElement("Capability");
        capability.addElement("type").addText(String.valueOf(this.paramType)).addAttribute("size", "1");
        capability.addElement("length").addText(String.valueOf(this.paramLen)).addAttribute("size", "1");
        // capability - Multiprotocol Extensions
        Element multiExt = capability.addElement("MultiProtocolExtensions");
        multiExt.addElement("type").addText(String.valueOf(this.capabilityCode)).addAttribute("size", "1");
        multiExt.addElement("length").addText(String.valueOf(this.capabilityLen)).addAttribute("size", "1");
        multiExt.addElement("afi").addText(String.valueOf(afi)).addAttribute("size", "2");
        multiExt.addElement("reserved").addText(String.valueOf(this.reserved)).addAttribute("size", "1");
        multiExt.addElement("safi").addText(String.valueOf(safi)).addAttribute("size", "1");
    }

    public int getAfi() {
        return afi;
    }

    public void setAfi(int afi) {
        this.afi = afi;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getSafi() {
        return safi;
    }

    public void setSafi(int safi) {
        this.safi = safi;
    }
}
