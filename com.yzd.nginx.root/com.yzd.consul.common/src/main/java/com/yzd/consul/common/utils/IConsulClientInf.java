package com.yzd.consul.common.utils;

import com.yzd.consul.common.entities.ServiceInfo;

import java.util.List;

public interface IConsulClientInf {
    /**
     * 通过服务标签找到健康的服务
     *
     * @param serviceTag
     */
    List<ServiceInfo> getAllHealthyServiceByServiceTag(String serviceTag);

    String add(ServiceInfo serviceInfo);

    String delete(ServiceInfo serviceInfo);
}
