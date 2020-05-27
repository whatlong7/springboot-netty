package com.picooc.wifiservice.backend.type;

import com.picooc.wifiservice.backend.Utils;
import com.picooc.wifiservice.domain.WifiLatinRouterMacPairedDataDO;
import com.picooc.wifiservice.dto.WifiLatinRouterMacPairedDataDTO;
import com.picooc.wifiservice.service.IWifiLatinRouterMacPairedDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Shawn Tien on 2/4/16.
 */

public class LoginType extends BaseType {
    private final static String replyType = "A8";
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Resource
    private IWifiLatinRouterMacPairedDataService wifiLatinRouterMacPairedDataService =
        (IWifiLatinRouterMacPairedDataService) ContextLoader.getCurrentWebApplicationContext()
            .getBean("wifiLatinRouterMacPairedDataService");

    @Override
    public boolean process() {
        String content = this.getContent();
        String macAddress = Utils.hexStringToMacAddress(content.substring(0, 0 + 6 * 2));
        String routerMacAddress = Utils.hexStringToMacAddress(content.substring(12, 12 + 6 * 2));
        String loginOk = Utils.hexStringToString(content.substring(24, 24 + 9 * 2));

        if (!loginOk.equals("+LOGIN_OK")) {
            logger.error("LOGIN_OK invalid: " + loginOk);
            this.setReply(this.errorResponseHex);
            return true;
        }

        try {
            WifiLatinRouterMacPairedDataDO data = new WifiLatinRouterMacPairedDataDO();
            data.setLatinMac(macAddress);
            data.setRouterMac(routerMacAddress);
            data.setBindTime((int) (System.currentTimeMillis() / 1000L));
            data.setTime(new Timestamp(new Date().getTime()));
            data.setLastUseRouterMac(routerMacAddress);


            /*
             * Check record (latinMac - routerMac pair) whether exist.
             * If exists, update by all record with this latinMac.
             * if NOT exists, just insert a new one.
             */
            WifiLatinRouterMacPairedDataDO query = wifiLatinRouterMacPairedDataService
                .getByLatinMacAndRouterMac(data.getLatinMac(), data.getRouterMac());
            if (query != null) {
                WifiLatinRouterMacPairedDataDTO dataDTO = new WifiLatinRouterMacPairedDataDTO();
                dataDTO.setLatinMac(macAddress);
                dataDTO.setRouterMac(routerMacAddress);
                wifiLatinRouterMacPairedDataService.update(dataDTO, data);
            } else {
                wifiLatinRouterMacPairedDataService.insert(data);
            }
        } catch (Exception e) {
            logger.error("Insert new login data to DB failed.");
            logger.error(e.getMessage());
            logger.error("Failed data is: " + "content: " + content + ", latin mac: " + macAddress + ", routerMac: "
                + routerMacAddress);
        }

        String prefix = this.getPrefix("04", replyType);
        String checksum = Utils.calculateChecksum(prefix);
        this.setReply(prefix + checksum);
        logger.info("Reply: " + prefix + checksum);

        return true;
    }
}
