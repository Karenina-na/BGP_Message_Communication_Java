package org.example.parsers.open;

import org.dom4j.Element;
import org.example.message.open.BGPOpen;
import org.example.message.open.open_opt.BGPOpen4OctAsNumberCap;
import org.example.message.open.open_opt.BGPOpenOpt;
import org.example.message.open.open_opt.BGPOpenOptMultiprotocolExtCap;
import org.example.message.open.open_opt.BGPOpenOptRouterRefreshCap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;


public class BGPOpenXmlParser {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPOpenXmlParser.class);

    public static BGPOpen parse(Element body){
        /*
        get version, asn, holdTime, id, optPara
        */
        Element version = body.element("version");
        Element asn = body.element("asn");
        Element holdTime = body.element("hold_time");
        Element id = body.element("id");
        // opt
        Vector<BGPOpenOpt> optPara = new Vector<>();
        List<Element> capabilities = body.element("opt").elements();
        for (Element cap : capabilities){
            Element type = cap.element("type");
            Element length = cap.element("length");
            // if type != 2
            if (Integer.parseInt(type.getText().strip()) != 2){
                LOGGER.error("Not a capability");
                return null;
            }
            // get 3rd element
            Element value = cap.elements().get(2).element("type");
            BGPOpenOpt opt = get_opt(Integer.parseInt(value.getText().strip()), cap);
            if (opt == null){
                LOGGER.error("Opt error");
                return null;
            }
            optPara.add(opt);
        }

        return new BGPOpen(
                Integer.parseInt(version.getText().strip()),
                Integer.parseInt(asn.getText().strip()),
                Integer.parseInt(holdTime.getText().strip()),
                id.getText().strip(),
                optPara
        );
    };

    public static BGPOpenOpt get_opt(int type, Element cap){
        BGPOpenOpt opt = null;
        int length = 0;

        switch (type){
            case 1:{
                // Multiprotocol Extensions
                Element mpe = cap.element("MultiProtocolExtensions");
                length = Integer.parseInt(mpe.element("length").getText().strip());
                Element afi = mpe.element("afi");
                Element safi = mpe.element("safi");
                opt = new BGPOpenOptMultiprotocolExtCap(
                        Integer.parseInt(afi.getText().strip()),
                        Integer.parseInt(safi.getText().strip())
                );
                break;
            }
            case 2:{
                // Route Refresh
                Element rr = cap.element("RouteRefresh");
                length = Integer.parseInt(rr.element("length").getText().strip());
                opt = new BGPOpenOptRouterRefreshCap();
                break;
            }
            case 65:{
                // 4-octet AS number
                Element asn = cap.element("FourOctetASNumber");
                length = Integer.parseInt(asn.element("length").getText().strip());
                Element asnNum = asn.element("asn");
                opt = new BGPOpen4OctAsNumberCap(
                        Integer.parseInt(asnNum.getText().strip())
                );
                break;
            }
            default:{
                LOGGER.error("Unknown capability type");
                break;
            }
        }

        // check null
        if (opt == null){
            LOGGER.error("Opt error");
            return null;
        }

        // check length
        if (length + 2 + 2 != opt.build_packet().length){
            LOGGER.error("Length error");
            return null;
        }

        return opt;
    }
}
