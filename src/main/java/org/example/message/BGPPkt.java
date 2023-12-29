package org.example.message;

import org.xml.sax.SAXException;

import java.io.IOException;

public interface BGPPkt {
    byte[] build_packet();

    String to_string();

    void write_to_xml(String path) throws IOException;
}
