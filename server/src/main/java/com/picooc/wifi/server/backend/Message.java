package com.picooc.wifi.server.backend;

//import com.picooc.wifi.backend.type.*;
import com.picooc.wifi.server.type.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by Shawn Tien on 2/2/16.
 */

public class Message {
    protected final Log logger = LogFactory.getLog(this.getClass());
    public String header;
    public String length;
    public String content;
    public String checksum;
    public String reply;

    public Message(String header, String length, String content, String checksum) {
        this.header = header;
        this.length = length;
        this.content = content;
        this.checksum = checksum;

        logger.info("Received message: " + this.header + this.length + this.content + this.checksum);

        // Check whether checksum is valid
        if (!isChecksumValid(this.header + this.length + this.content, this.checksum)) {
            this.reply = BaseType.errorResponseHex;

            logger.error("Invalid message, checksum is invalid");
            logger.error("Hex to be checked: " + this.header + this.length + this.content);
            logger.error("Checksum is: " + this.checksum);
            return;
        }

        logger.info("header: " + header + " content: " + content);
        switch (this.header) {
            /*
             * A5消息: 要求UTC对时
             */
            case "A5":
                SyncUTCType syncUTC = new SyncUTCType();
                syncUTC.setContent(this.content);
                syncUTC.process();
                this.reply = syncUTC.getReply();
                break;
            /*
             * A6消息: 发送称重数据
             */
            case "A6":
                OneWeightType weight = new OneWeightType();
                weight.setContent(this.content);
                weight.process();
                this.reply = weight.getReply();
                break;
            /*
             * A7消息: 发送历史数据
             */
            case "A7":
                HistoryWeightType hWeight = new HistoryWeightType();
                hWeight.setContent(this.content);
                hWeight.process();
                this.reply = hWeight.getReply();
                break;
            /*
             * A8消息: 发送登录信息
             */
            case "A8":
                LoginType login = new LoginType();
                login.setContent(this.content);
                login.process();
                this.reply = login.getReply();
                break;
            /*
             * A9消息: 发送多条称重消息
             */
            case "A9":
                BulkWeightType bWeight = new BulkWeightType();
                bWeight.setContent(this.content);
                bWeight.process();
                this.reply = bWeight.getReply();
                break;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        return new BigInteger(s, 16).toByteArray();
    }

    public Byte[] getBytesB() {
        byte[] bytes = hexStringToByteArray(this.reply);

        Byte[] byteObjects = new Byte[bytes.length];

        int i = 0;
        for (byte b : bytes)
            byteObjects[i++] = b;  // Autoboxing.

        int j = 0;
        for (Byte b : byteObjects)
            bytes[j++] = b.byteValue();

        return byteObjects;
    }

    public byte[] getBytes() {
        byte[] bytes = hexStringToByteArray(this.reply);
        return Arrays.copyOfRange(bytes, 1, bytes.length);
    }

    private String readFixedLengthByte(InputStream inputStream, int length) {
        Byte[] byteArray = new Byte[length];
        int c;
        int i = 0;
        while (i < length) {
            try {
                c = inputStream.read();
                if (c >= 0) {
                    byteArray[i] = (byte) c;
                } else {
                    continue;
                }
            } catch (IOException e) {
                logger.error("Error reade inputStream");
                logger.error(e.getMessage());
            }

            i++;
        }
        return com.picooc.wifi.server.backend.Utils.byteArrayToHexString(byteArray);
    }

    public boolean isChecksumValid(String hexString, String checksum) {
        if (com.picooc.wifi.server.backend.Utils.calculateChecksum(hexString).equals(checksum)) {
            return true;
        }
        return false;
    }

}
