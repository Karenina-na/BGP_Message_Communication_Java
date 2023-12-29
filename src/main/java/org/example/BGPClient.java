package org.example;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.VoidFunc0;
import org.example.message.BGPPkt;
import org.example.message.keeplive.BGPKeepLive;
import org.example.message.notification.BGPNotification;
import org.example.message.notification.BGPNotificationErrorCode;
import org.example.message.notification.BGPNotificationSubErrorCode;
import org.example.message.open.BGPOpen;
import org.example.message.open.open_opt.BGPOpen4OctAsNumberCap;
import org.example.message.open.open_opt.BGPOpenOptMultiprotocolExtCap;
import org.example.message.open.open_opt.BGPOpenOptRouterRefreshCap;
import org.example.message.update.BGPUpdate;
import org.example.message.update.BGPUpdateNLRI;
import org.example.message.update.path_attr.BGPUpdateAttrAS_PATH;
import org.example.message.update.path_attr.BGPUpdateAttrMED;
import org.example.message.update.path_attr.BGPUpdateAttrNEXT_HOP;
import org.example.message.update.path_attr.BGPUpdateAttrORIGIN;
import org.example.parsers.BGPParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;


public class BGPClient {
    private static final String src_ip = "192.168.10.1";
    private static final int src_port = 179;
    private static final int src_asn = 500;
    private static final String des_ip = "192.168.10.5";
    private static final int des_port = 179;

    public static void main(String[] args) throws IOException, InterruptedException {
        // 并发安全队列
        Queue<byte[]> pipe = new ConcurrentLinkedQueue<>();

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(des_ip, des_port));

        // create receive thread
        Thread t = new Thread(() -> {
            receive(pipe, socket);
        });
        t.start();


        // process and send
        byte[] buffer;
        Vector<BGPPkt> result;

        // open
//        BGPOpen open = new BGPOpen(4, src_asn, 180, src_ip, null);
        BGPOpen open = new BGPOpen(4, src_asn, 180, src_ip,
                new Vector<>(){{
                    add(new BGPOpenOptMultiprotocolExtCap(1, 1));
                    add(new BGPOpenOptRouterRefreshCap());
                    add(new BGPOpen4OctAsNumberCap(500));
                }}
        );
        socket.getOutputStream().write(open.build_packet());
        do {
            buffer = pipe.poll();
            Thread.sleep(1);
        } while (buffer == null);
        result = BGPParser.parse(buffer);    // open + keep live
        for (BGPPkt pkt : result) {
            System.out.println(pkt.to_string());
        }

        // keep live
        BGPKeepLive keepLive = new BGPKeepLive();
        socket.getOutputStream().write(keepLive.build_packet());
        do {
            buffer = pipe.poll();
            Thread.sleep(1);
        } while (buffer == null);
        result = BGPParser.parse(buffer);   // update + update
        for (BGPPkt pkt : result) {
            System.out.println(pkt.to_string());
        }

        // update
        BGPUpdate bgp_up = new BGPUpdate(false,
                new Vector<>(){{
                    add(new BGPUpdateAttrORIGIN((byte) 0x40, 0));   // ORIGIN
                    add(new BGPUpdateAttrAS_PATH((byte) 0x40, new Vector<>(){{  // AS_PATH
                        add(src_asn);
                    }}));
                    add(new BGPUpdateAttrNEXT_HOP((byte) 0x40, "192.168.10.1"));  // NEXT_HOP
                    add(new BGPUpdateAttrMED((byte) 0x80, 0));  // Multi Exit Disc
                }},
                new Vector<>(){{
                    add(new BGPUpdateNLRI(24, "12.0.0.0")); // NLRI
                }}
        );
        socket.getOutputStream().write(bgp_up.build_packet());
        do {
            buffer = pipe.poll();
            Thread.sleep(1);
        } while (buffer == null);
        result = BGPParser.parse(buffer);   // keep live
        for (BGPPkt pkt : result) {
            System.out.println(pkt.to_string());
        }

        // update
        BGPUpdate bgp_up_draw = new BGPUpdate(true,
                new Vector<>(), // no path attr
                new Vector<>(){{
                    add(new BGPUpdateNLRI(24, "12.0.0.0")); // withdrawn
                }}
        );
        socket.getOutputStream().write(bgp_up_draw.build_packet());
        do {
            buffer = pipe.poll();
            Thread.sleep(1);
        } while (buffer == null);
        result = BGPParser.parse(buffer);   // keep live
        for (BGPPkt pkt : result) {
            System.out.println(pkt.to_string());
        }

        // notification
        BGPNotification bgp_nt = new BGPNotification(BGPNotificationErrorCode.UPDATEMessageError, BGPNotificationSubErrorCode.MalformedASPath);
        socket.getOutputStream().write(bgp_nt.build_packet());
        do {
            buffer = pipe.poll();
            Thread.sleep(1);
        } while (buffer == null);

        socket.close();

        t.interrupt();
    }

    public static void receive(Queue<byte[]> pipe, Socket socket){
        byte[] buffer;
        while (true){   // 读取线程
            buffer = new byte[2048];
            try {
                int len = socket.getInputStream().read(buffer);
                if (len == -1){
                    pipe.add(new String("Socket closed").getBytes());
                    break;
                }
                pipe.add(buffer);
            } catch (IOException e) {
                // Socket closed
                if (e.getMessage().equals("Socket closed")){
                    break;
                }else {
                    e.printStackTrace();
                }
            }
        }
    }
}
