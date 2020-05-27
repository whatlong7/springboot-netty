package com.picooc.wifi.server.type;

import com.picooc.wifi.server.backend.Record;
import com.picooc.wifi.server.backend.ScaleDomain;
import com.picooc.wifi.server.backend.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Shawn Tien on 2/4/16.
 */

public class BulkWeightType extends BaseType {
    private final static String replyType = "A9";
    private static final Logger log = LoggerFactory.getLogger(BulkWeightType.class);
    private ScaleDomain scaleDomain = new ScaleDomain();

    @Override
    public boolean process() {
        String domainPreSet = scaleDomain.getDomainPreSet();

        String content = this.getContent();
        Integer length = content.length();
        String macAddress = Utils.hexStringToMacAddress(content.substring(0, 12));
        String separator = "0D0A"; //\x0D0A = hex(\r\n)

        for (int i = 12; i < length; i += 22) {
            // is_normal 1 表示当前上传的数据, 0 表示历史测量数据
            Integer is_normal = null;
            String subContent = content.substring(i, i + 22);

            char flag = (char) Integer.parseInt(subContent.substring(0, 2), 16);
            Long unixTime = Long.parseLong(subContent.substring(2, 10), 16);
            Double weight = (double) Integer.parseInt(subContent.substring(10, 14), 16) / 20.0F;
            Integer resistance = Integer.parseInt(subContent.substring(14, 18), 16) / 10;
            String sign = subContent.substring(18, 22);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append("mac: ").append(macAddress);
            logMessage.append("flag: ").append(flag);
            logMessage.append(", unixTime: ").append(unixTime);
            logMessage.append(", Date: ").append(Utils.convertSecondsToDateString(unixTime));
            logMessage.append(", weight: ").append(weight);
            logMessage.append(", resistance: ").append(resistance);
            logMessage.append(", domain: ").append(domainPreSet);
            logMessage.append(", sign: ").append(sign);
            logger.info("Unpacking bulk weight: " + logMessage.toString());

            if (flag == 'O' && sign.equals("0D0A")) {
                is_normal = 0;
            } else if (flag == 'N' && sign.equals("0D0A")) {
                is_normal = 1;
            } else {
                logger.info("Invalid weight, ignore");
                continue;
            }

            Record record = new Record(macAddress, unixTime, weight, resistance, is_normal, domainPreSet);
            record.revise();

            if (!record.isValid()) {
                logger.info("Record is not valid, ignored.");
                logger.info("Ignored record is : " + logMessage);
            } else {

                if (putDataToMessageQueue(record)) {
                    logger.info("Push one of bulk weight datas to SQS queue success");
                    logger.info("Success data: " + logMessage);
                } else {
                    logger.error("Push one of bulk weight datas to SQS queue failed");
                    logger.error("Failed data: " + logMessage);
                }
            }
        }

        String prefix = this.getPrefix("04", replyType);
        String checksum = Utils.calculateChecksum(prefix);
        this.setReply(prefix + checksum);
        logger.info("Reply: " + prefix + checksum);

        return true;
    }
}
