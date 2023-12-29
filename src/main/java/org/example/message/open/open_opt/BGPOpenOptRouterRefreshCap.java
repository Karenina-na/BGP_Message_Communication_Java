package org.example.message.open.open_opt;

import cn.hutool.core.convert.Convert;

public class BGPOpenOptRouterRefreshCap implements BGPOpenOpt{
    @Override
    public byte[] build_packet() {
        /*
        type code: 1 byte
        length: 1 byte
        type: 1 byte
        length: 1 byte
        * */
        byte[] packet = new byte[4];
        // type code
        packet[0] = (byte) 0x02;
        // length
        packet[1] = (byte) 0x02;
        // type
        packet[2] = (byte) 0x02;
        // length
        packet[3] = (byte) 0x00;

        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Opt Param: Capability (" + 0x02 + ")\n";
        result += "  - type code: 0x" + Convert.toHex(new byte[] {(byte) 0x02}) + "\n";
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) 0x02}) + "\n";
        result += "  - capability: Route Refresh (" + 0x02 + ")\n";
        return result;
    }
}
