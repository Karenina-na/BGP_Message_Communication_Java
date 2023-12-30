package org.example.parsers.notification;

import org.dom4j.Element;
import org.example.message.notification.BGPNotification;
import org.example.message.notification.BGPNotificationErrorCode;
import org.example.message.notification.BGPNotificationSubErrorCode;


public class BGPNotificationXmlParser {
    public static BGPNotification parse(Element body){
        /*
        get errorCode, errorSubCode
        */
        Element errorCode = body.element("major_error_code");
        Element errorSubCode = body.element("minor_error_code");

        BGPNotificationErrorCode majarcode = BGPNotificationErrorCode.fromValue(Integer.parseInt(errorCode.getText().strip()));
        BGPNotificationSubErrorCode subcode = BGPNotificationSubErrorCode.fromValue(majarcode, Integer.parseInt(errorSubCode.getText().strip()));
        return new BGPNotification(majarcode, subcode);
    };
}
