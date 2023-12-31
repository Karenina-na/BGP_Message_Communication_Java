package org.example.message.open.open_opt;

import org.dom4j.Element;

public class BGPOpenOptRouterRefreshCap extends BGPOpenOptAbc implements BGPOpenOpt {

    public BGPOpenOptRouterRefreshCap() {
        this.paramType = 2;
        this.paramLen = 2;
        this.capabilityCode = 2;
        this.capabilityLen = 0;
    }
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
        packet[0] = (byte) this.paramType;
        // length
        packet[1] = (byte) this.paramLen;
        // type
        packet[2] = (byte) this.capabilityCode;
        // length
        packet[3] = (byte) this.capabilityLen;

        return packet;
    }

    @Override
    public String to_string() {
        String result = "- Opt Param: Capability (" + this.paramType + ")\n";
        result += "  - length: " + this.paramLen + "\n";
        result += "  - capability: Route Refresh (" + this.capabilityCode + ")\n";
        result += "    - length: " + capabilityLen + "\n";
        return result;
    }

    @Override
    public void set_xml(Element opt) {
        // opt - Capability
        Element capability = opt.addElement("Capability");
        capability.addElement("type").addText(String.valueOf(this.paramType)).addAttribute("size", "1");
        capability.addElement("length").addText(String.valueOf(this.paramLen)).addAttribute("size", "1");
        // capability - Route Refresh
        Element route_refresh = capability.addElement("RouteRefresh");
        route_refresh.addElement("type").addText(String.valueOf(this.capabilityCode)).addAttribute("size", "1");
        route_refresh.addElement("length").addText(String.valueOf(this.capabilityLen)).addAttribute("size", "1");
    }
}
