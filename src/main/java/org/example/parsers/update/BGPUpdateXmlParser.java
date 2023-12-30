package org.example.parsers.update;

import org.dom4j.Element;
import org.example.message.update.BGPUpdate;
import org.example.message.update.BGPUpdateNLRI;
import org.example.message.update.path_attr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Vector;


public class BGPUpdateXmlParser {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPUpdateXmlParser.class);

    public static BGPUpdate parse(Element body){
        /*
        get withdrawnLen, withdrawn, pathAttrLen, pathAttr, nlri
        */
        Element withdrawnLen = body.element("withdrawn_len");
        // widthdrawn routes
        Vector<BGPUpdateNLRI> NLRI = new Vector<>();
        if (Integer.parseInt(withdrawnLen.getText().strip()) != 0){
            Element withdrawnRoutes = body.element("withdrawn_routes");
            for (Element withdrawn_route : withdrawnRoutes.elements()){
                NLRI.add(new BGPUpdateNLRI(
                                Integer.parseInt(withdrawn_route.element("prefix_len").getText().strip()),
                                withdrawn_route.element("prefix").getText().strip()
                        )
                );
            }
        }
        // pathAttr
        Element total_pathAttrLen = body.element("total_path_attr_len");
        Vector<BGPUpdatePathAttr> pathAttrVector = new Vector<>();
        if (Integer.parseInt(total_pathAttrLen.getText().strip()) != 0){
            Element pathAttr = body.element("path_attr");
            for (Element attr : pathAttr.elements()){
                Element type_code = attr.element("type_code");
                BGPUpdatePathAttr attr_obj = get_attr(Integer.parseInt(type_code.getText().strip()), attr);
                if (attr_obj == null){
                    LOGGER.error("Path attr error");
                    return null;
                }
                pathAttrVector.add(attr_obj);
            }
        }
        // nlri
        if (Integer.parseInt(withdrawnLen.getText().strip()) == 0){
            Element nlris = body.element("nlris");
            for (Element nlri : nlris.elements()){
                NLRI.add(new BGPUpdateNLRI(
                                Integer.parseInt(nlri.element("prefix_len").getText().strip()),
                        nlri.element("prefix").getText().strip()
                        )
                );
            }
        }

        if (Integer.parseInt(withdrawnLen.getText().strip()) == 0){
            return new BGPUpdate(false, pathAttrVector, NLRI);
        } else {
            return new BGPUpdate(true, pathAttrVector, NLRI);
        }
    };

    private static BGPUpdatePathAttr get_attr(int type, Element attr){
        byte flags = (byte) Integer.parseInt(
                attr.element("flags").getText().strip().substring(2),
                16
        );
        int length = Integer.parseInt(attr.element("length").getText().strip());
        BGPUpdatePathAttr attr_obj = null;

        switch (type) {
            case 1: {
                // ORIGIN
                Element origin = attr.element("origin");
                attr_obj = new BGPUpdateAttrORIGIN(
                        flags,
                        Integer.parseInt(origin.getText().strip())
                );
                break;
            }
            case 2: {
                // AS_PATH
                Element as_path = attr.element("as_path_seg");
                // from 2nd element
                Vector<Integer> as_vector = new Vector<>();
                for (Element as : as_path.elements().subList(2, as_path.elements().size())){
                    as_vector.add(Integer.parseInt(as.getText().strip()));
                }
                attr_obj = new BGPUpdateAttrAS_PATH(
                        flags,
                        as_vector
                );
                break;
            }
            case 3: {
                // NEXT_HOP
                Element next_hop = attr.element("next_hop");
                attr_obj = new BGPUpdateAttrNEXT_HOP(
                        flags,
                        next_hop.getText().strip()
                );
                break;
            }
            case 4: {
                // MULTI_EXIT_DISC
                Element med = attr.element("med");
                attr_obj = new BGPUpdateAttrMED(
                        flags,
                        Integer.parseInt(med.getText().strip())
                );
                break;
            }
            default:{
                LOGGER.error("Unknown path attr type");
                break;
            }
        }

        // check null
        if (attr_obj == null){
            LOGGER.error("Path attr error");
            return null;
        }

        // check length
        if (length + 2 + 1 != attr_obj.build_packet().length){
            LOGGER.error("Path attr length error");
            return null;
        }

        return attr_obj;
    }
}
