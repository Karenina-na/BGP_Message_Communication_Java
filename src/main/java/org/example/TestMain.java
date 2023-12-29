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
import org.example.message.update.*;
import org.example.message.update.path_attr.BGPUpdateAttrAS_PATH;
import org.example.message.update.path_attr.BGPUpdateAttrMED;
import org.example.message.update.path_attr.BGPUpdateAttrNEXT_HOP;
import org.example.message.update.path_attr.BGPUpdateAttrORIGIN;
import org.example.parsers.BGPParser;

import java.util.Vector;

public class TestMain {


    public static void main(String[] args) {
        test_bgp_packet_parse();
    }

    // 测试 BGP 包解析器
    public static void test_bgp_packet_parse(){
        BGPOpen bgp_op = new BGPOpen(4, 500, 180, "192.168.10.1",
                new Vector<>(){{
                    add(new BGPOpenOptMultiprotocolExtCap(1, 1));
                    add(new BGPOpenOptRouterRefreshCap());
                    add(new BGPOpen4OctAsNumberCap(500));
                }}
        );
        byte[] packet1 = bgp_op.build_packet();

        BGPKeepLive bgp_kl = new BGPKeepLive();
        byte[] packet2 = bgp_kl.build_packet();

        //noinspection ExtractMethodRecommender
        BGPUpdate bgp_up = new BGPUpdate(false,
                new Vector<>(){{
                    add(new BGPUpdateAttrORIGIN((byte) 0x40, 0));   // ORIGIN
                    add(new BGPUpdateAttrAS_PATH((byte) 0x40, new Vector<>(){{  // AS_PATH
                        add(100);
                    }}));
                    add(new BGPUpdateAttrNEXT_HOP((byte) 0x40, "192.168.10.5"));  // NEXT_HOP
                    add(new BGPUpdateAttrMED((byte) 0x80, 0));  // Multi Exit Disc
                }},
                new Vector<>(){{
                    add(new BGPUpdateNLRI(24, "10.0.0.0")); // NLRI
                }}
        );
        byte[] packet3 = bgp_up.build_packet();

        BGPUpdate bgp_up_draw = new BGPUpdate(true,
                new Vector<>(), // no path attr
                new Vector<>(){{
                    add(new BGPUpdateNLRI(24, "11.0.0.0")); // withdrawn
                }}
        );
        byte[] packet4 = bgp_up_draw.build_packet();

//        BGPNotification bgp_nt = new BGPNotification(6, 5);
        BGPNotification bgp_nt = new BGPNotification(BGPNotificationErrorCode.Cease, BGPNotificationSubErrorCode.ConnectionRejected);
        byte[] packet5 = bgp_nt.build_packet();


        // 构造一个 BGP 包
        byte[] packet_r = new byte[packet1.length + 4 +packet2.length + 4 + packet3.length + 4 + packet4.length + 4 + packet5.length];

        // 拼接
        System.arraycopy(packet1, 0, packet_r, 0, packet1.length);
        System.arraycopy(packet2, 0, packet_r, packet1.length + 4, packet2.length);
        System.arraycopy(packet3, 0, packet_r, packet1.length + 4 + packet2.length + 4, packet3.length);
        System.arraycopy(packet4, 0, packet_r, packet1.length + 4 + packet2.length + 4 + packet3.length + 4, packet4.length);
        System.arraycopy(packet5, 0, packet_r, packet1.length + 4 + packet2.length + 4 + packet3.length + 4 + packet4.length + 4, packet5.length);

        System.out.println(Convert.toHex(packet_r).equals(  // 与标准值比较
                "ffffffffffffffffffffffffffffffff001d010401f400b4c0a80a0100" + "00000000" +
                "ffffffffffffffffffffffffffffffff001304" + "00000000" +
                "ffffffffffffffffffffffffffffffff003402000000194001010040020402010064400304c0a80a0580040400000000180a0000" + "00000000" +
                "ffffffffffffffffffffffffffffffff001b020004180b00000000" + "00000000" +
                "ffffffffffffffffffffffffffffffff0015030605"
        ));


        // 解析
        Vector<BGPPkt> result = BGPParser.parse(packet_r);
        for (BGPPkt pkt : result) {
            System.out.println(pkt.to_string());
        }

        // 再次构造
        byte[] packet_r2 = new byte[packet1.length + packet2.length + packet3.length + packet4.length + packet5.length];
        int pointer = 0;
        for (BGPPkt pkt : result) {
            byte[] pkt_bytes = pkt.build_packet();
            System.arraycopy(pkt_bytes, 0, packet_r2, pointer, pkt_bytes.length);
            pointer += pkt_bytes.length;
        }
        byte[] packet_r2_cut = new byte[pointer];
        System.arraycopy(packet_r2, 0, packet_r2_cut, 0, pointer);

        // 比较
        System.out.println(Convert.toHex(packet_r2_cut).equals(
                "ffffffffffffffffffffffffffffffff001d010401f400b4c0a80a0100" +
                "ffffffffffffffffffffffffffffffff001304" +
                "ffffffffffffffffffffffffffffffff003402000000194001010040020402010064400304c0a80a0580040400000000180a0000" +
                "ffffffffffffffffffffffffffffffff001b020004180b00000000" +
                "ffffffffffffffffffffffffffffffff0015030605"
        ));
    }
}