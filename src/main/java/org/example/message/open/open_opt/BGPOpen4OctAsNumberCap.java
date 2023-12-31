package org.example.message.open.open_opt;

import org.dom4j.Element;

public class BGPOpen4OctAsNumberCap extends BGPOpenOptAbc implements BGPOpenOpt {
    private int asn;

    public BGPOpen4OctAsNumberCap(int asn) {
        this.paramType = 2;
        this.paramLen = 6;
        this.capabilityCode = 65;
        this.capabilityLen = 4;
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
        packet[0] = (byte) this.paramType;
        // length
        packet[1] = (byte) this.paramLen;
        // type
        packet[2] = (byte) this.capabilityCode;
        // length
        packet[3] = (byte) this.capabilityLen;
        // asn
        packet[4] = (byte) (asn >> 24);
        packet[5] = (byte) ((asn >> 16) & 0xff);
        packet[6] = (byte) ((asn >> 8) & 0xff);
        packet[7] = (byte) (asn & 0xff);

        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Opt Param: Capability (" + paramType + ")\n";
        result += "  - length: " + paramLen + "\n";
        result += "  - capability: 4-octet AS number (" + capabilityCode + ")\n";
        result += "    - length: " + capabilityLen + "\n";
        result += "    - asn: " + asn + "\n";
        return result;
    }

    @Override
    public void set_xml(Element opt) {
        // opt - Capability
        Element capability = opt.addElement("Capability");
        capability.addElement("type").addText(String.valueOf(this.paramType)).addAttribute("size", "1");
        capability.addElement("length").addText(String.valueOf(this.paramType)).addAttribute("size", "1");
        // capability - 4-octet AS number
        Element four_octet_as_number = capability.addElement("FourOctetASNumber");
        four_octet_as_number.addElement("type").addText(String.valueOf(this.capabilityCode)).addAttribute("size", "1");
        four_octet_as_number.addElement("length").addText(String.valueOf(this.capabilityLen)).addAttribute("size", "1");
        four_octet_as_number.addElement("asn").addText(String.valueOf(asn)).addAttribute("size", "4");
    }

    public int getAsn() {
        return asn;
    }

    public void setAsn(int asn) {
        this.asn = asn;
    }
}
