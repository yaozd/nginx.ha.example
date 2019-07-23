package com.yzd.consul.common.entities;

public enum MonitorType {
    M_JVM("spring boot"),
    M_REDIS("redis"),
    //网关类型
    M_GATEWAY("nginx-api-gateway");

    MonitorType(String desc) {

    }
}
