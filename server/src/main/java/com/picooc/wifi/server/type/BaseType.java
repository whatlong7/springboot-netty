package com.picooc.wifi.server.type;

import com.picooc.wifi.server.backend.Record;
//import com.picooc.wifi.backend.queue.WifiWeightQueue;
//import com.picooc.wifiservice.backend.queue.httpsqs.Httpsqs4j;
//import com.picooc.wifiservice.backend.queue.httpsqs.HttpsqsClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Shawn Tien on 2/4/16.
 */

public class BaseType {
    public final static String errorResponseHex = "F104000A";
    private final static String prefix = "F108A5";
//    private final static HttpsqsClient client = Httpsqs4j.createNewClient();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String reply;
    private String content;

    protected String getPrefix(String length, String replyType) {
        /*
         * Prefix definition
         * F1: reply header
         * length: length of reply message
         * replyType: Type of message
         *
         * Example: F108A5
         */
        return "F1" + length + replyType;
    }

    /**
     * 把数据放到队列中
     * @param record
     * @return
     */
    public boolean putDataToMessageQueue(Record record) {
        // # TODO：whatlong  这里原本是要把数据放到队列中去，先注释掉，后续再改造

        System.out.println(record);

//        logger.debug("Push data to HTTPSQS.");
//        WifiWeightQueue queue = new WifiWeightQueue();
//        queue.setMac(record.getMac());
//        queue.setTime(record.getTime());
//        queue.setWeight(record.getWeight());
//        queue.setResistance(record.getResistance());
//        queue.setIsNormal(record.getIsNormal());
//        queue.setDomain(record.getDomain());
//
//        if (!queue.put()) {
//            return false;
//        }
        return true;
    }

    public double getWeightFromHex(String hexString) {
        Double weight = (double) Integer.parseInt(hexString, 16);
        return weight / 2.0 / 10.0;
    }

    public double getImpedanceFromHex(String hexString) {
        Double impedance = (double) Integer.parseInt(hexString, 16);
        return impedance / 10.0;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean process() {
        return true;
    }
}
