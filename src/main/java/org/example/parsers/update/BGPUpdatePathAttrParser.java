package org.example.parsers.update;

import org.example.message.update.path_attr.*;

import java.util.Vector;

public class BGPUpdatePathAttrParser{

    public static Vector<BGPUpdatePathAttr> parse (byte[] pkt) {
        // 报文切片
        int point = 0;
        Vector<BGPUpdatePathAttr> result = new Vector<>();

        while (point < pkt.length){
            byte flag = pkt[point];
            byte type = pkt[point + 1];
            int length = pkt[point + 2] & 0xff;

            process_value(pkt, type, result, flag, point + 3, length);

            point += 3 + length;
        }

        return result;
    }

    private static void process_value(byte[] pkt, byte type, Vector<BGPUpdatePathAttr> result,
                                        byte flag, int point, int length) {
        // value: point ~ point + length
        switch (type){
            case 1:{
                // ORIGIN
                result.add(new BGPUpdateAttrORIGIN(flag, pkt[point]));
                break;
            }
            case 2:{
                // AS_PATH
                Vector<Integer> as_path = new Vector<>();
                int as_path_length = pkt[point + 1] & 0xff;     // TODO: default segment type is 2，this ignores the first byte
                int as_path_point = point + 2;
                for (int i = 0; i < as_path_length; i++) {
                    // 2 字节
                    as_path.add((pkt[as_path_point + i * 2] << 8) + (pkt[as_path_point + i * 2 + 1] & 0xff));
                }
                result.add(new BGPUpdateAttrAS_PATH(flag, as_path));
                break;
            }
            case 3:{
                // NEXT_HOP
                StringBuilder next_hop = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    next_hop.append(pkt[point + i] & 0xff);
                    next_hop.append(".");
                }
                next_hop.deleteCharAt(next_hop.length() - 1);
                result.add(new BGPUpdateAttrNEXT_HOP(flag, next_hop.toString()));
                break;
            }
            case 4:{
                // Multi Exit Disc - 4 字节
                int med = (pkt[point] << 24) + (pkt[point + 1] << 16) + (pkt[point + 2] << 8) + (pkt[point + 3] & 0xff);
                result.add(new BGPUpdateAttrMED(flag, med));
                break;
            }
        }
    }

}
