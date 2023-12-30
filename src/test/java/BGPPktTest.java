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
import org.example.message.refresh.BGPRefresh;
import org.example.message.update.BGPUpdate;
import org.example.message.update.BGPUpdateNLRI;
import org.example.message.update.path_attr.BGPUpdateAttrAS_PATH;
import org.example.message.update.path_attr.BGPUpdateAttrMED;
import org.example.message.update.path_attr.BGPUpdateAttrNEXT_HOP;
import org.example.message.update.path_attr.BGPUpdateAttrORIGIN;
import org.example.parsers.BGPPktParser;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BGPPktTest {

    // open
    private final BGPOpen bgp_op = new BGPOpen(4, 500, 180, "192.168.10.1",
            new Vector<>(){{
                add(new BGPOpenOptMultiprotocolExtCap(1, 1));
                add(new BGPOpenOptRouterRefreshCap());
                add(new BGPOpen4OctAsNumberCap(500));
            }}
    );

    // keep live
    private final BGPKeepLive bgp_kl = new BGPKeepLive();

    // update nlri
    private final BGPUpdate bgp_up = new BGPUpdate(false,
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

    // update withdraw
    private final BGPUpdate bgp_up_draw = new BGPUpdate(true,
            new Vector<>(), // no path attr
            new Vector<>(){{
                add(new BGPUpdateNLRI(24, "11.0.0.0")); // withdrawn
            }}
    );

    // notification
    private final BGPNotification bgp_nt = new BGPNotification(BGPNotificationErrorCode.Cease, BGPNotificationSubErrorCode.ConnectionRejected);

    // refresh
    private final BGPRefresh bgp_rf = new BGPRefresh(1, 1);

    // packet map
    private final Map<String, String> packet_map = new HashMap<>(){{
        // bgp_op
        put("bgp_op", "ffffffffffffffffffffffffffffffff0031010401f400b4c0a80a011402060104000100010202020002064104000001f4");
        // bgp_kl
        put("bgp_kl", "ffffffffffffffffffffffffffffffff001304");
        // bgp_up
        put("bgp_up", "ffffffffffffffffffffffffffffffff003402000000194001010040020402010064400304c0a80a0580040400000000180a0000");
        // bgp_up_draw
        put("bgp_up_draw", "ffffffffffffffffffffffffffffffff001b020004180b00000000");
        // bgp_nt
        put("bgp_nt", "ffffffffffffffffffffffffffffffff0015030605");
        // bgp_rf
        put("bgp_rf", "ffffffffffffffffffffffffffffffff00170500010001");
    }};
    private final Vector<Object> packet_vector = new Vector<>(){{
        add(Convert.hexToBytes(packet_map.get("bgp_op")));
        add(Convert.hexToBytes(packet_map.get("bgp_kl")));
        add(Convert.hexToBytes(packet_map.get("bgp_up")));
        add(Convert.hexToBytes(packet_map.get("bgp_up_draw")));
        add(Convert.hexToBytes(packet_map.get("bgp_nt")));
        add(Convert.hexToBytes(packet_map.get("bgp_rf")));
    }};

    // 解析器
    private final BGPPktParser BGPPktParser = new BGPPktParser();

    @Test
    public void testOpen() {
        byte[] packet = bgp_op.build_packet();
        assert Convert.toHex(packet).equals(packet_map.get("bgp_op"));
    }

    @Test
    public void testKeepLive() {
        byte[] packet = bgp_kl.build_packet();
        assert Convert.toHex(packet).equals(packet_map.get("bgp_kl"));
    }

    @Test
    public void testUpdate() {
        byte[] packet = bgp_up.build_packet();
        assert Convert.toHex(packet).equals(packet_map.get("bgp_up"));
    }

    @Test
    public void testUpdateDraw() {
        byte[] packet = bgp_up_draw.build_packet();
        assert Convert.toHex(packet).equals(packet_map.get("bgp_up_draw"));
    }

    @Test
    public void testNotification() {
        byte[] packet = bgp_nt.build_packet();
        assert Convert.toHex(packet).equals(packet_map.get("bgp_nt"));
    }

    @Test
    public void testRefresh() {
        byte[] packet = bgp_rf.build_packet();
        assert Convert.toHex(packet).equals(packet_map.get("bgp_rf"));
    }

    @Test
    public void testParse() {
        Vector<Object> objects = new Vector<>() {{
            add(bgp_op.build_packet());
            add(bgp_kl.build_packet());
            add(bgp_up.build_packet());
            add(bgp_up_draw.build_packet());
            add(bgp_nt.build_packet());
            add(bgp_rf.build_packet());
        }};

        // 间隔 4 字节的空白
        byte[] packet = new byte[objects.stream().mapToInt(o -> ((byte[]) o).length).sum() + 4 * objects.size()];

        // 拼接
        int pointer = 0;
        for (Object o : objects) {
            byte[] bytes = (byte[]) o;
            System.arraycopy(bytes, 0, packet, pointer, bytes.length);
            pointer += bytes.length + 4;
        }

        // 与标准的baseline填充4字节空白比较
        byte[] packet_baseline = new byte[packet_vector.stream().mapToInt(o -> ((byte[]) o).length).sum() + 4 * packet_vector.size()];

        // 拼接
        pointer = 0;
        for (Object o : packet_vector) {
            byte[] bytes = (byte[]) o;
            System.arraycopy(bytes, 0, packet_baseline, pointer, bytes.length);
            pointer += bytes.length + 4;
        }

        // 比较
        assert Convert.toHex(packet).equals(Convert.toHex(packet_baseline));

        // 解析 -- 并分割
        Vector<BGPPkt> result = BGPPktParser.parse(packet);

        // 转换为字节数组
        byte[] packet_r = new byte[result.stream().mapToInt(o -> ((byte[]) o.build_packet()).length).sum()];
        pointer = 0;
        for (BGPPkt pkt : result) {
            byte[] pkt_bytes = pkt.build_packet();
            System.arraycopy(pkt_bytes, 0, packet_r, pointer, pkt_bytes.length);
            pointer += pkt_bytes.length;
        }

        // baseline -- 无空白版
        byte[] packet_baseline_r = new byte[packet_vector.stream().mapToInt(o -> ((byte[]) o).length).sum()];
        pointer = 0;
        for (Object o : packet_vector) {
            byte[] bytes = (byte[]) o;
            System.arraycopy(bytes, 0, packet_baseline_r, pointer, bytes.length);
            pointer += bytes.length;
        }

        // 比较
        assert Convert.toHex(packet_r).equals(Convert.toHex(packet_baseline_r));
    }

}
