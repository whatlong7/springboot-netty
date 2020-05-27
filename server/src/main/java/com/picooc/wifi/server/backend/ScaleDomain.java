package com.picooc.wifi.server.backend;

import com.picooc.wifi.server.common.DomainEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleDomain {
    private static final Logger log = LoggerFactory.getLogger(ScaleDomain.class);

    // 与预先设置的域名比较, 是否合格 只是在工厂生产环境下使用, 所以可以多线程共享一个变量
    public static boolean allow = false;

    // 记录当前生产的秤的域名: 只是在工厂生产环境下使用, 所以可以多线程共享一个变量
    private static String domainPreSet = "";

    public String domainDefault = "unknown";

    public String convertDomainFromCode(int code) {
        return DomainEnum.domainMap.getOrDefault(code, domainDefault);
    }

    /**
     * 与预先设置的域名比较, 相同则合格, 不相同则秤不合格
     * @param domain
     * @return
     */
    public boolean isAllow(String domain) {
        if (this.getDomainPreSet().equals(domain)) {
            this.allow = true;
            return true;
        } else {
            log.info("domain is mismatch. domain: {} domainPreSet: {}. set it with /v1/api/socket/setDomain?domain={} if you want to pass it", domain, domainPreSet, domain);
            this.allow = false;
            return false;
        }
    }

    public String getDomainPreSet() {
        return domainPreSet;
    }

    public void setDomainPreSet(String domainPreSet) {
        this.domainPreSet = domainPreSet;
    }

    public boolean isAllow() {
        return allow;
    }

}
