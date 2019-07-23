package com.yzd.consul.service.consul;

import com.yzd.consul.common.entities.ServiceInfo;

import java.util.List;

public interface IConsulService {
    void add(ServiceInfo serviceInfo);

    void delete(String serviceId);

    /**
     * 通过服务名称找到健康的服务
     *
     * @param serviceName
     */
    List<ServiceInfo> getAllHealthyServiceByServiceName(String serviceName);

    /**
     * 通过服务标签找到健康的服务
     *
     * @param serviceTag
     */
    List<ServiceInfo> getAllHealthyServiceByServiceTag(String serviceTag);

    /**
     *
     * @param serviceInfo
     */
    void putValue4Upstream(ServiceInfo serviceInfo);

    /**
     *
     * @param serviceInfo
     */
    void deleteValue4Upstream(ServiceInfo serviceInfo);
}
