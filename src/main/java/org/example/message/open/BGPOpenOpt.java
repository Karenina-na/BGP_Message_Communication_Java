package org.example.message.open;

public interface BGPOpenOpt {   // todo: extends BGPPkt
    byte[] build_packet();
    String to_string();
}
