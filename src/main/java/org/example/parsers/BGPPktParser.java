package org.example.parsers;

import org.example.message.BGPPkt;
import org.example.message.BGPType;
import org.example.parsers.keeplive.BGPKeepLiveParser;
import org.example.parsers.notification.BGPNotificationParser;
import org.example.parsers.open.BGPOpenParser;
import org.example.parsers.refresh.BGPRefreshParser;
import org.example.parsers.update.BGPUpdateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class BGPPktParser implements BGPParser{
    protected static final Logger LOGGER = LoggerFactory.getLogger(BGPPktParser.class);

    // 解析器
    public Vector<BGPPkt> parse(byte[] packet) {
        Vector<byte[]> packets = split_packet(packet);
        Vector<BGPPkt> result = new Vector<>();
        for (byte[] pkt : packets) {
            BGPType type = get_type(pkt);
            switch (type) {
                case OPEN:
                    result.add(BGPOpenParser.parse(pkt));
                    break;
                case UPDATE:
                    result.add(BGPUpdateParser.parse(pkt));
                    break;
                case NOTIFICATION:
                    result.add(BGPNotificationParser.parse(pkt));
                    break;
                case KEEPALIVE:
                    result.add(BGPKeepLiveParser.parse(pkt));
                    break;
                case REFRESH:
                    result.add(BGPRefreshParser.parse(pkt));
                    break;
                default:
                    LOGGER.error("Unknown BGP packet type");
                    break;
            }
        }
        return result;
    }

    // 分割数据包
    private Vector<byte[]>  split_packet(byte[] packet) {
        // marker: 16 bytes
        int point = 0;
        Vector<byte[]> packets = new Vector<>();
        boolean flag = true;

        while (point < packet.length - 1) { // point - 数据包起始
            // 检查 marker
            flag = false;
            while (point < packet.length - 1) {
                if (packet[point] == (byte) 0xff) {
                    // 检查当前 point 到 point+15 是否都是 0xff
                    byte[] tmp = new byte[16];
                    System.arraycopy(packet, point, tmp, 0, 16);
                    if (check(tmp)) {
                        flag = true;
                        break;
                    }
                }
                point++;
            }
            if (!flag) {
                break;
            }
            int len = (packet[point + 16] << 8) + (packet[point + 16 + 1] & 0xff);    // 包长度
            byte[] tmp = new byte[len];
            // 从 point 到 point+len-1
            System.arraycopy(packet, point, tmp, 0, len);
            packets.add(tmp);
            point += len;   // 移动指针
        }
        return packets;
    }

    // check packet
    private boolean check(byte[] packet){
        for (int i = 0; i < 16; i++) {
            if (packet[i] != (byte) 0xff) {
                return false;
            }
        }
        return true;
    }

    // get type
    private BGPType get_type(byte[] packet) {
        switch (packet[18]) {
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
