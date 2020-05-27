package com.picooc.wifi.server.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Shawn Tien on 2/26/16.
 */
public final class Utils {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


    public final static Integer diffAbs(Integer foo, Integer bar) {
        return Math.abs(foo - bar);
    }

    public final static Long diffAbs(Long foo, Long bar) {
        return Math.abs(foo - bar);
    }

    public final static Float diffAbs(Float foo, Float bar) {
        return Math.abs(foo - bar);
    }

    public final static Double diffAbs(Double foo, Double bar) {
        return Math.abs(foo - bar);
    }

    public static String hexStringToMacAddress(String hexString) {
        int length = hexString.length();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (char c : hexString.toCharArray()) {
            if (index > 1) {
                sb.append(':');
                index = 0;
            }
            sb.append(c);
            index++;
        }
        return sb.toString();
    }

    public static String execCommandLine(String command) {
        String line;
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                output += (line + '\n');
            }
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return output;
    }

    public static Double round(Double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public static String convertSecondsToDateString(Long seconds) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(seconds * 1000L);
        return formatter.format(cal.getTime()).toString();
    }

    public static String byteArrayToHexString(Byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String hexStringToString(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            sb.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return sb.toString();
    }

    public static String calculateChecksum(String hex) {
        String[] splits = hex.split("(?<=\\G.{2})");
        int sumInteger = 0;
        String binaryString;
        String newBinaryString = "";
        String checksumHex;

        for (int i = 0; i < splits.length; i++) {
            sumInteger += Integer.parseInt(splits[i], 16);
        }

        binaryString = Integer.toBinaryString(sumInteger);
        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == '0') {
                newBinaryString += '1';
            } else {
                newBinaryString += '0';
            }
        }

        checksumHex = Integer.toHexString(Integer.parseInt(newBinaryString, 2));

        // Only get last two chars
        checksumHex = "0" + checksumHex; //left completion with 0, if checksumHex is single Char
        checksumHex = checksumHex.substring(Math.max(checksumHex.length() - 2, 0));
        return checksumHex.toUpperCase();
    }

    public static Integer timestampToUnixtime(Timestamp timestamp) {
        Long unixTime = timestamp.getTime() / 1000L;
        return unixTime.intValue();
    }

    public static Timestamp unixtimeTotimestamp(Long unixTime) {
        return new Timestamp(unixTime * 1000L);
    }

    public static boolean isSameDaytimeZone(Timestamp foo, Timestamp bar) {
        if (getTimestampHourZone(foo) == getTimestampHourZone(bar)) {
            return true;
        } else {
            return false;
        }
    }

    public static Integer getTimestampHourZone(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());

        Integer zone = null;
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour <= 3) {
        } else if (hour >= 4 && hour <= 10) {
            zone = 1;
        } else if (hour >= 11 && hour <= 15) {
            zone = 2;
        } else if (hour >= 16 && hour <= 19) {
            zone = 3;
        } else if (hour >= 20 && hour <= 23) {
            zone = 4;
        } else {
            zone = 5;
        }

        return zone;
    }
}
