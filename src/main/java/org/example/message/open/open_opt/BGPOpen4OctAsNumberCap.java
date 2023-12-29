package org.example.message.open.open_opt;

import cn.hutool.core.convert.Convert;

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
}
