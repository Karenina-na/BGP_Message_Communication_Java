package org.example.message.update;

public class BGPUpdateNLRI {

    private final int prefixLen;
    private final String prefix;

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

    String to_string() {
        String s = "- Domain: " + prefix + "/" + prefixLen + "\n";
        s += "  - prefixLen: " + prefixLen + "\n";
        s += "  - prefix: " + prefix + "\n";
        return s;
    }
}
