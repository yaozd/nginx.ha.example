package com.yzd.consul.common.utils;

import com.yzd.consul.common.entities.MonitorType;
import com.yzd.consul.common.entities.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

@Slf4j
public class IConsulClientInfTest {

    IConsulClientInf consulClientInf;

    @Before
    public void init() {

        String url = "http://127.0.0.1:27680";
        consulClientInf = new ConsulClientImpl(url);
    }

    @Test
    public void getAllHealthyServiceByServiceTag() {
        List<ServiceInfo> list4ServiceInfo = consulClientInf.getAllHealthyServiceByServiceTag(MonitorType.M_JVM.name());
        log.info(list4ServiceInfo.toString());
    }

    @Test
    public void add() {
        ServiceInfo serviceInfo;
        String name = "com.example.actuator2";
        String ip = "127.0.0.1";
        Integer port = 8080;
        Long interval = 3l;
        serviceInfo = ServiceInfo.newBuilder()
                .name(name)
                .ip(ip)
                .port(port)
                .checkUrl(ip + ":" + port)
                .checkInterval(interval)
                .tag(MonitorType.M_JVM.name())
                .build();
        String result = consulClientInf.add(serviceInfo);
        log.info(result);
    }
}