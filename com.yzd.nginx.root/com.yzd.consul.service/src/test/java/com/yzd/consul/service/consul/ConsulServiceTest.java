package com.yzd.consul.service.consul;

import com.yzd.consul.common.entities.MonitorType;
import com.yzd.consul.common.entities.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import startup.Application4Consul;

import java.util.List;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application4Consul.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ConsulServiceTest {

    @Autowired
    IConsulService consulService;
    ServiceInfo serviceInfo;

    @Before
    public void init() {
        String name = "com.example.actuator";
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
    }

    @Test
    public void add() {
        consulService.add(serviceInfo);
    }

    @Test
    public void getAllHealthyServiceByServiceName() {
        List<ServiceInfo> list4ServiceInfo = consulService.getAllHealthyServiceByServiceName(serviceInfo.getName());
        log.info(list4ServiceInfo.toString());
    }

    @Test
    public void getAllHealthyServiceByServiceTag() {
        List<ServiceInfo> list4ServiceInfo = consulService.getAllHealthyServiceByServiceTag(MonitorType.M_JVM.name());
        log.info(list4ServiceInfo.toString());
    }
}