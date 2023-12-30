package org.example.parsers.refresh;

import org.dom4j.Element;
import org.example.message.refresh.BGPRefresh;


public class BGPRefreshXmlParser {
    public static BGPRefresh parse(Element body){
        /*
        afi, safi
        */
        Element afi = body.element("afi");
        Element safi = body.element("safi");
        return new BGPRefresh(
                Integer.parseInt(afi.getText().strip()),
                Integer.parseInt(safi.getText().strip())
        );
    };
}
