package org.example.message.update;

import org.dom4j.Element;

public class BGPUpdateNLRI {

    private int prefixLen;
    private String prefix;

    public BGPUpdateNLRI(int prefixLen, String prefix) {
        this.prefixLen = prefixLen;
        this.prefix = prefix;
    }

    byte[] build_packet() {
        /*
        prefixLen: 1 byte
        prefix: prefixLen bytes
        * */
        byte[] packet = new byte[1 + prefixLen / 8];    // 1 + prefixLen / 8
        packet[0] = (byte) prefixLen;
        String[] split = prefix.split("\\.");
        for (int i = 0; i < prefixLen / 8; i++) {
            packet[i + 1] = (byte) Integer.parseInt(split[i]);
        }
        return packet;
    }

    public String to_string() {
        String s = "- Domain: " + prefix + "/" + prefixLen + "\n";
        s += "  - prefixLen: " + prefixLen + "\n";
        s += "  - prefix: " + prefix + "\n";
        return s;
    }


    public void set_xml(Element route) {
        route.addElement("prefix_len").addText(String.valueOf(prefixLen)).addAttribute("size", "1");
        route.addElement("prefix").addText(prefix).addAttribute("size", String.valueOf(prefixLen / 8));
    }

    public int getPrefixLen() {
        return prefixLen;
    }

    public void setPrefixLen(int prefixLen) {
        this.prefixLen = prefixLen;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
