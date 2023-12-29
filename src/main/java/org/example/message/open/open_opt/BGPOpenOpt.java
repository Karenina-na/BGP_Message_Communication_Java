package org.example.message.open.open_opt;

public interface BGPOpenOpt {   // todo: extends BGPPkt
    byte[] build_packet();
    String to_string();
}
