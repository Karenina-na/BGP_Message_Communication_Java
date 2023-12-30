package org.example.message.update.path_attr;

import org.dom4j.Element;

public interface BGPUpdatePathAttr {
    public byte[] build_packet();
    public String to_string();

    public void set_xml(Element attr);

}
