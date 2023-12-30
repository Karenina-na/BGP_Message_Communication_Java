package org.example.message.open.open_opt;

import cn.hutool.core.convert.Convert;
import org.dom4j.Element;

public class BGPOpenOptRouterRefreshCap implements BGPOpenOpt{
    @Override
    public byte[] build_packet() {
        /*
        type code: 1 byte
        length: 1 byte
        type: 1 byte
        length: 1 byte
        * */
        byte[] packet = new byte[4];
        // type code
        packet[0] = (byte) 0x02;
        // length
        packet[1] = (byte) 0x02;
        // type
        packet[2] = (byte) 0x02;
        // length
        packet[3] = (byte) 0x00;

        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Opt Param: Capability (" + 0x02 + ")\n";
        result += "  - type code: 0x" + Convert.toHex(new byte[] {(byte) 0x02}) + "\n";
        result += "  - length: 0x" + Convert.toHex(new byte[] {(byte) 0x02}) + "\n";
        result += "  - capability: Route Refresh (" + 0x02 + ")\n";
        return result;
    }

    @Override
    public void set_xml(Element opt) {
        // opt - Capability
        Element capability = opt.addElement("Capability");
        capability.addElement("type").addText("2").addAttribute("size", "1");
        capability.addElement("length").addText("2").addAttribute("size", "1");
        // capability - Route Refresh
        Element route_refresh = capability.addElement("RouteRefresh");
        route_refresh.addElement("type").addText("2").addAttribute("size", "1");
        route_refresh.addElement("length").addText("0").addAttribute("size", "1");
    }
}
