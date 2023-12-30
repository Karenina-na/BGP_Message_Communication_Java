package org.example;

import cn.hutool.core.convert.Convert;
import org.example.message.BGPPkt;
import org.example.message.keeplive.BGPKeepLive;
import org.example.message.notification.BGPNotification;
import org.example.message.notification.BGPNotificationErrorCode;
import org.example.message.notification.BGPNotificationSubErrorCode;
import org.example.message.open.BGPOpen;
import org.example.message.open.open_opt.BGPOpen4OctAsNumberCap;
import org.example.message.open.open_opt.BGPOpenOptMultiprotocolExtCap;
import org.example.message.open.open_opt.BGPOpenOptRouterRefreshCap;
import org.example.message.refresh.BGPRefresh;
import org.example.message.update.*;
import org.example.message.update.path_attr.BGPUpdateAttrAS_PATH;
import org.example.message.update.path_attr.BGPUpdateAttrMED;
import org.example.message.update.path_attr.BGPUpdateAttrNEXT_HOP;
import org.example.message.update.path_attr.BGPUpdateAttrORIGIN;
import org.example.parsers.BGPParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Vector;

public class TestMain {

    public static void main(String[] args) throws IOException, SAXException {
        BGPOpen bgp_op = new BGPOpen(4, 500, 180, "192.168.10.1",
                new Vector<>(){{
                    add(new BGPOpenOptMultiprotocolExtCap(1, 1));
                    add(new BGPOpenOptRouterRefreshCap());
                    add(new BGPOpen4OctAsNumberCap(500));
                }}
        );
        bgp_op.write_to_xml("open.xml");
        System.out.println(bgp_op.to_string());
    }
}