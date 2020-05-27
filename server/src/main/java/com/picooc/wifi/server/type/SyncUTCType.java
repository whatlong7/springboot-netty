package com.picooc.wifiservice.backend.type;

import com.picooc.wifiservice.backend.ScaleDomain;
import com.picooc.wifiservice.backend.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Shawn Tien on 2/4/16.
 */

public class SyncUTCType extends BaseType {
    private final static String replyType = "A5";
    private static final Logger log = LoggerFactory.getLogger(SyncUTCType.class);
    private ScaleDomain scaleDomain = new ScaleDomain();

    @Override
    public boolean process() {
        /*
         * generate UTC timestamp
         */
        String prefix = this.getPrefix("08", replyType);

        /*if (!isPresetDomain()) {
            this.setReply(BaseType.errorResponseHex);
            return false;
        }*/

        String utc_timestamp;
        String checksum;

        utc_timestamp = Long.toHexString(new Date().getTime() / 1000L);
        checksum = Utils.calculateChecksum(prefix + utc_timestamp);
        this.setReply(prefix + utc_timestamp + checksum);

        // 回复的格式如下
        // 00F108A55D2D248330
        // 00F1 08 A5 5D2D2483 30
        // 其中 5D2D2483 是时间, 在 bash 下可以通过printf '%d\n' 0x5D2D2483 转换为可读的时间格式

        return true;
    }

    private boolean isPresetDomain() {
        final String content = this.getContent();
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        String macAddress = Utils.hexStringToMacAddress(content.substring(0, 12));

//        A5 0a D0 49 00 01 02 03  01 30  国内
//        A5 0a D0 49 00 01 02 02  02 30  国外
//        带域名com的对时 data  A50aD049000102030130 content    D0490001020301
//        带域名us的对时  data  A50aD049000102020230 content    D0490001020202

        if (content.length() == 14) {
            int code = Integer.parseInt(content.substring(12, 14), 16);
            String domain = scaleDomain.convertDomainFromCode(code);
            if (!scaleDomain.isAllow(domain)) {
                log.info("domain is mismatch. mac {} domainPreSet: {} domain: {}", macAddress, scaleDomain.getDomainPreSet(), domain);
                return false;
            }
        } else {
            log.warn("no domain info mac {}", macAddress);
            return false;
        }

        return true;
    }
}
