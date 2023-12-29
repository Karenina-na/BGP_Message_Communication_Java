package org.example.parsers.open;

import org.example.message.open.open_opt.BGPOpen4OctAsNumberCap;
import org.example.message.open.open_opt.BGPOpenOpt;
import org.example.message.open.open_opt.BGPOpenOptMultiprotocolExtCap;
import org.example.message.open.open_opt.BGPOpenOptRouterRefreshCap;

import java.util.Vector;

public class BGPOpenPathOptParser {
    public static Vector<BGPOpenOpt> parse (byte[] packet) {
        // 报文切片
        int point = 0;
        Vector<BGPOpenOpt> result = new Vector<>();

        while (point < packet.length){
            byte type = packet[point];
            byte length = packet[point + 1];

            if (type != 2){ // 2 is the type code of capability
                continue;
            }
            // point + 4: is the start of the value, point + 3: is the length of the value
            process_value(packet, packet[point + 2], result, point + 4);

            point += 2 + length;
        }

        return result;
    }

    private static void process_value(byte[] packet, byte type, Vector<BGPOpenOpt> result, int i) {
        // value: point ~ point + length
        switch (type){
            case 1:{
                // Multiprotocol Extensions
                int afi = (packet[i] << 8) + (packet[i + 1] & 0xff);
                int safi = packet[i + 3] & 0xff;
                result.add(new BGPOpenOptMultiprotocolExtCap(afi, safi));
                break;
            }
            case 2:{
                // Route Refresh
                result.add(new BGPOpenOptRouterRefreshCap());
                break;
            }
            case 65:{
                // 4-octet AS number
                int asn = (packet[i] << 24) + ((packet[i + 1] & 0xff) << 16) + ((packet[i + 2] & 0xff) << 8) + (packet[i + 3] & 0xff);
                result.add(new BGPOpen4OctAsNumberCap(asn));
                break;
            }
            default:{
                break;
            }
        }
    }
}
