package org.example.message.update.path_attr;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;
import java.util.Vector;

public class BGPUpdateAttrAS_PATH implements BGPUpdatePathAttr{
    private byte flags;   // default 0x40
    private int type_code;  // default 0x02
    private int length;
    private int seg_type_code;  // default 0x02
    private int seg_length;
    private Vector<Integer> as_path;

    public BGPUpdateAttrAS_PATH(byte flags, Vector<Integer> asPath) {
        this.flags = flags;
        this.type_code = 0x02;
        this.length = 2 + asPath.size() * 2;
        this.seg_type_code = 0x02;
        this.seg_length = asPath.size();
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
        byte[] packet = new byte[3 + length];
        packet[0] = flags;
        packet[1] = (byte) type_code;
        packet[2] = (byte) length;
        packet[3] = (byte) seg_type_code;    // as_path type code 2 AS_SEQUENCE
        packet[4] = (byte) seg_length;  // as_path length
        for (int i = 0; i < seg_length; i++) {
            packet[5 + i * 2] = (byte) (as_path.get(i) >> 8);
            packet[6 + i * 2] = (byte) (as_path.get(i) & 0xff);
        }
        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Attr AS_PATH: \n";
        result += "  - flags: 0x" + Convert.toHex(new byte[] {flags}) + "\n";
        result += "  - type code: " + type_code + "\n";
        result += "  - length: " + length + "\n";
        result += "  - as_path: \n";
        result += "    - type code: " + seg_type_code + "\n";
        result += "    - length:" + seg_length + "\n";
        StringBuilder resultBuilder = new StringBuilder(result);
        for (int i = 0; i < seg_length; i++) {
            resultBuilder.append("    - as ").append(i).append(": ").append(as_path.get(i)).append("\n");
        }
        result = resultBuilder.toString();
        return result;
    }

    @Override
    public void set_xml(Element attr) {
        Element as_path = attr.addElement("as_path");
        as_path.addElement("flags").addText("0x" + Convert.toHex(new byte[] {flags})).addAttribute("size", "1");
        as_path.addElement("type_code").addText(String.valueOf(type_code)).addAttribute("size", "1");
        as_path.addElement("length").addText(String.valueOf(length)).addAttribute("size", "1");
        // as_path_seg
        Element as_path_seg = as_path.addElement("as_path_seg");
        as_path_seg.addElement("type_code").addText( String.valueOf(seg_type_code)).addAttribute("size", "1"); // as_path type code 2 AS_SEQUENCE
        as_path_seg.addElement("length").addText( String.valueOf(seg_length)).addAttribute("size", "1");  // as_path length
        for (Integer integer : this.as_path) {
            as_path_seg.addElement("as2").addText(String.valueOf(integer)).addAttribute("size", "2");
        }
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public int getType_code() {
        return type_code;
    }

    public void setType_code(int type_code) {
        this.type_code = type_code;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSeg_type_code() {
        return seg_type_code;
    }

    public void setSeg_type_code(int seg_type_code) {
        this.seg_type_code = seg_type_code;
    }

    public int getSeg_length() {
        return seg_length;
    }

    public void setSeg_length(int seg_length) {
        this.seg_length = seg_length;
    }

    public Vector<Integer> getAs_path() {
        return as_path;
    }

    public void setAs_path(Vector<Integer> as_path) {
        this.as_path = as_path;
    }
}
