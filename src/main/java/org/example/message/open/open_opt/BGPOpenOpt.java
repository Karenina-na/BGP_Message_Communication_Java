package org.example.message.open.open_opt;

import org.dom4j.Element;

public interface BGPOpenOpt {
    byte[] build_packet();
    String to_string();

    void set_xml(Element opt);
}
