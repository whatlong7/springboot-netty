package com.picooc.wifiservice.common;

import java.util.HashMap;

public enum DomainEnum {

    COM(1, "com"),
    US(2, "us");
    
    public int getCode() {
        return this.code;
    }
    
    public String getDomain() {
        return this.domain;
    }

    private DomainEnum(int code, String domain) {
        this.code = code;
        this.domain = domain;
    }
    
    private int code;
    private String domain;
    public static HashMap<Integer, String>  domainMap = new HashMap<>();
    
    static {
        for (DomainEnum domain : DomainEnum.values()) {
            domainMap.put(domain.getCode(), domain.getDomain());
        }
    }
    
    public static void main(String[] args) {
        System.out.println(domainMap);
        
        System.out.println(DomainEnum.COM.getCode());
        System.out.println(DomainEnum.COM.getDomain());
        
        for (DomainEnum name : DomainEnum.values()) {
            System.out.println(name + " : " + name.getDomain());
        }
        
    }

}