package com.picooc.wifi.server.type;

import com.picooc.wifi.server.backend.Record;
import com.picooc.wifi.server.backend.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Shawn Tien on 2/4/16.
 */

public class HistoryWeightType extends BaseType {
    private final static String replyType = "A7";
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public boolean process() {
        String content = this.getContent();
        String macAddress = Utils.hexStringToMacAddress(content.substring(0, 12));
        Long unixTime = Long.parseLong(content.substring(12, 20), 16);
        Double weight = (double) Integer.parseInt(content.substring(20, 24), 16) / 20.0F;
        Integer resistance = Integer.parseInt(content.substring(24, 28), 16) / 10;
        Integer is_normal = 0;

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("mac: ").append(macAddress);
        logMessage.append("unixTime: ").append(unixTime);
        logMessage.append(", weight: ").append(weight);
        logMessage.append(", resistance: ").append(resistance);
        logMessage.append(", is_normal: ").append(is_normal);

        Record record = new Record(macAddress, unixTime, weight, resistance, is_normal);
        record.revise();

        if (!record.isValid()) {
            logger.info("Record is not valid, ignored.");
            logger.info("Ignored record is : " + logMessage);
        } else {

            if (putDataToMessageQueue(record)) {
                logger.info("Push one of history weight datas to SQS queue success.");
                logger.info("Success data: " + logMessage);
            } else {
                logger.error("Push one of history weight datas to SQS queue failed.");
                logger.error("Failed data: " + logMessage.toString());
            }
        }

        String prefix = this.getPrefix("04", replyType);
        String checksum = Utils.calculateChecksum(prefix);
        this.setReply(prefix + checksum);
        logger.info("Reply: " + prefix + checksum);

        return true;
    }
}
