package com.picooc.wifi.server.type;

import com.picooc.wifi.server.backend.Record;
import com.picooc.wifi.server.backend.ScaleDomain;
import com.picooc.wifi.server.backend.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Shawn Tien on 2/4/16.
 */

public class OneWeightType extends BaseType {
    private static final Logger log = LoggerFactory.getLogger(OneWeightType.class);
    
    private final static String replyType = "A6";
    private ScaleDomain scaleDomain = new ScaleDomain();

    @Override
    public boolean process() {

//    A6 11 01 00 00 00 D0 49 00 01 02 03 01 01 02 01 23 -----没带域名
//    A6 12 01 00 00 00 D0 49 00 01 02 03 01 01 02 01 01 21 --国内
//    A6 12 01 00 00 00 D0 49 00 01 02 03 01 01 02 01 02 20 --国外

//    A61101000000D049000102030101020123 #A6没带域名
//    A61201000000D04900010203010102010121 #A6国内
//    A61201000000D04900010203010102010220 #A6国外

        String domainPreSet = scaleDomain.getDomainPreSet();

        String content = this.getContent();
        String macAddress = Utils.hexStringToMacAddress(content.substring(0, 12));
        Long unixTime = Long.parseLong(content.substring(12, 20), 16);
        Double weight = (double) Integer.parseInt(content.substring(20, 24), 16) / 20.0F;
        Integer resistance = Integer.parseInt(content.substring(24, 28), 16) / 10;

        Integer is_normal = 1;

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("mac: ").append(macAddress);
        logMessage.append(", unixTime: ").append(unixTime);
        logMessage.append(", Date: ").append(Utils.convertSecondsToDateString(unixTime));
        logMessage.append(", weight: ").append(weight);
        logMessage.append(", resistance: ").append(resistance);
        logMessage.append(", domain: ").append(domainPreSet);
        logMessage.append(", is_normal: ").append(is_normal);
        

        Record record = new Record(macAddress, unixTime, weight, resistance, is_normal, domainPreSet);
        record.revise();

        if (!record.isValid()) {
            logger.info("Record is not valid, ignored.");
            logger.info("Ignored record is : " + logMessage);
        } else {

            if (putDataToMessageQueue(record)) {
                logger.info("Push one of one-weight datas to SQS queue success.");
                logger.info("Success data: " + logMessage.toString());
            } else {
                logger.error("Push one of one-weight weight datas to SQS queue failed.");
                logger.error("Failed data: " + logMessage.toString());
            }
        }


        String prefix = this.getPrefix("04", replyType);
        String checksum = Utils.calculateChecksum(prefix);
        this.setReply(prefix + checksum);

        return true;
    }

}
