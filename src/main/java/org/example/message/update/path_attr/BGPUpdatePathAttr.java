package org.example.message.update.path_attr;

import org.dom4j.Element;

public interface BGPUpdatePathAttr {
    byte[] build_packet();
    String to_string();
    void set_xml(Element attr);

}
