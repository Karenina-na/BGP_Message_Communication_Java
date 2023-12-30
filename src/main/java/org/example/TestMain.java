package org.example;

import org.dom4j.DocumentException;
import org.example.message.BGPPkt;
import org.example.parsers.BGPXmlParser;

public class TestMain {

    public static void main(String[] args) throws DocumentException {

        BGPXmlParser bgpXmlParser = new BGPXmlParser();
        BGPPkt bgpPkt = bgpXmlParser.parse("xml/update_nlri.xml");
        System.out.println(bgpPkt.to_string());
    }
}