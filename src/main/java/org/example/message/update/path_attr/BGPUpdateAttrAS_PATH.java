package org.example.message.update.path_attr;

import cn.hutool.core.convert.Convert;

import java.util.Vector;

public class BGPUpdateAttrAS_PATH implements BGPUpdatePathAttr{
    private final byte flags;   // default 0x40
    private final Vector<Integer> as_path;

    public BGPUpdateAttrAS_PATH(byte flags, Vector<Integer> asPath) {
        this.flags = flags;
        this.as_path = asPath;
    }


    @Override
    public byte[] build_packet() {
        /*
        flags: 1 byte
        type code: 1 byte
        length: 1 byte
        as_path 基本元素2bit，一个as 2byte
        * */
        byte[] packet = new byte[3 + 2 + as_path.size() * 2];
        packet[0] = flags;
        packet[1] = (byte) 0x02;
        packet[2] = (byte) (2 + as_path.size() * 2);
        packet[3] = (byte) 0x02;    // as_path type code 2 AS_SEQUENCE
        packet[4] = (byte) as_path.size();  // as_path length
        for (int i = 0; i < as_path.size(); i++) {
            packet[5 + i * 2] = (byte) (as_path.get(i) >> 8);
            packet[6 + i * 2] = (byte) (as_path.get(i) & 0xff);
        }
        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Attr AS_PATH: \n";
        result += "  - flags: 0x" + Convert.toHex(new byte[] {flags}) + "\n";
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) (2 + as_path.size() * 2)}) + "\n";
        result += "  - as_path: \n";
        result += "    - type code: 0x" + Convert.toHex(new byte[] {(byte) 0x02}) + "\n";
        result += "    - length: 0x" + Convert.toHex(new byte[] {(byte) as_path.size()}) + "\n";
        StringBuilder resultBuilder = new StringBuilder(result);
        for (int i = 0; i < as_path.size(); i++) {
            resultBuilder.append("    - as ").append(i).append(": ").append(as_path.get(i)).append("\n");
        }
        result = resultBuilder.toString();
        return result;
    }
}
