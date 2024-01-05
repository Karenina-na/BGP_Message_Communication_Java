import org.dom4j.DocumentException;
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
import org.example.parsers.BGPXmlParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class BGPXmlTest {

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

    // path
    private final String path = "./xml";

    // file_name
    private final Vector<String> file_name = new Vector<>(){{
        add("open.xml");
        add("keep_live.xml");
        add("update_nlri.xml");
        add("update_withdraw.xml");
        add("notification.xml");
        add("refresh.xml");
    }};

    // check xml
    @Before
    public void check_xml_path(){
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }
    }


    @Test
    public void testOpen() throws IOException {
        bgp_op.write_to_xml(path + "/open.xml");
    }

    @Test
    public void testKeepLive() throws IOException {
        bgp_kl.write_to_xml(path + "/keep_live.xml");
    }

    @Test
    public void testUpdateNLRI() throws IOException {
        bgp_up.write_to_xml(path + "/update_nlri.xml");
    }

    @Test
    public void testUpdateWithdraw() throws IOException {
        bgp_up_draw.write_to_xml(path + "/update_withdraw.xml");
    }

    @Test
    public void testNotification() throws IOException {
        bgp_nt.write_to_xml(path + "/notification.xml");
    }

    @Test
    public void testRefresh() throws IOException {
        bgp_rf.write_to_xml(path + "/refresh.xml");
    }

    // check parser
    @Test
    public void testParser(){
        BGPXmlParser parser = new BGPXmlParser();

        file_name.forEach(file -> {
            try {
                BGPPkt pkt = parser.parse(path + "/" + file, true);
                switch (file_name.indexOf(file)){
                    case 0: {
                        assert Arrays.equals(pkt.build_packet(), bgp_op.build_packet());
                        break;
                    }
                    case 1: {
                        assert Arrays.equals(pkt.build_packet(), bgp_kl.build_packet());
                        break;
                    }
                    case 2: {
                        assert Arrays.equals(pkt.build_packet(), bgp_up.build_packet());
                        break;
                    }
                    case 3: {
                        assert Arrays.equals(pkt.build_packet(), bgp_up_draw.build_packet());
                        break;
                    }
                    case 4: {
                        assert Arrays.equals(pkt.build_packet(), bgp_nt.build_packet());
                        break;
                    }
                    case 5: {
                        assert Arrays.equals(pkt.build_packet(), bgp_rf.build_packet());
                        break;
                    }
                }
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
