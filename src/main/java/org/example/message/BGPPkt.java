package org.example.message;

public interface BGPPkt {
    byte[] build_packet();

    String to_string();
}
