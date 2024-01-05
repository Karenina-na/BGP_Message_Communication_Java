package org.example.parsers;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.example.message.BGPPkt;
import org.example.message.BGPType;
import org.example.parsers.keeplive.BGPKeepLiveXmlParser;
import org.example.parsers.notification.BGPNotificationXmlParser;
import org.example.parsers.open.BGPOpenXmlParser;
import org.example.parsers.refresh.BGPRefreshXmlParser;
import org.example.parsers.update.BGPUpdateXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BGPXmlParser implements BGPParser{
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPXmlParser.class);

    // 解析器
    public BGPPkt parse(String path) throws DocumentException {
        return parse(path, true);
    }

    // 解析器 -- 重载
    public BGPPkt parse(String path, boolean valid) throws  DocumentException{
        SAXReader reader = new SAXReader();
        Element root = reader.read(path).getRootElement();

        // header
        Element header = root.element("header");
        Element marker = header.element("marker");

        if (valid){ //  check marker
            boolean check = check(marker.getText().strip());
            if (!check) {
                LOGGER.error("Marker error");
                return null;
            }
        }

        Element length = header.element("length");
        Element type = header.element("type");

        // body
        Element body = root.element("body");
        BGPPkt pkt = null;
        switch (get_type(type.getText().strip())) {
            case OPEN:
                pkt =  BGPOpenXmlParser.parse(body);
                break;
            case UPDATE:
                pkt = BGPUpdateXmlParser.parse(body);
                break;
            case NOTIFICATION:
                pkt = BGPNotificationXmlParser.parse(body);
                break;
            case KEEPALIVE:
                pkt = BGPKeepLiveXmlParser.parse(body);
                break;
            case REFRESH:
                pkt = BGPRefreshXmlParser.parse(body);
                break;
            default:
                LOGGER.error("Unknown BGP packet type");
                break;
        }

        // check pkt
        if (pkt == null) {
            LOGGER.error("Parse error");
            return null;
        }

        // check length
        if (valid){
            if (pkt.build_packet().length != Integer.parseInt(length.getText().strip())) {
                LOGGER.error("Length error");
                return null;
            }
        }

        return pkt;
    }

    // check packet
    private boolean check(String marker){
        if (marker.length() != 32) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            if (marker.charAt(i * 2) != 'f' || marker.charAt(i * 2 + 1) != 'f') {
                return false;
            }
        }
        return true;
    }

    // get type
    private BGPType get_type(String type) {
        switch (Integer.parseInt(type)) {
            case 1:
                return BGPType.OPEN;
            case 2:
                return BGPType.UPDATE;
            case 3:
                return BGPType.NOTIFICATION;
            case 4:
                return BGPType.KEEPALIVE;
            case 5:
                return BGPType.REFRESH;
            default:
                return BGPType.UNKNOWN;
        }
    }
}
