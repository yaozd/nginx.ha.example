package com.yzd.consul.service.consul;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.State;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.ServiceHealth;
import com.yzd.consul.common.entities.ServiceInfo;
import com.yzd.consul.common.entities.UpstreamInfo;
import com.yzd.consul.common.utils.FastJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ConsulService implements IConsulService {
    @Value("${consul.server.host}")
    private String host;
    @Value("${consul.server.port}")
    private Integer port;
    private Consul client;

    @PostConstruct
    public void initConnection() {
        log.info("Initializing connection with consul http://" + host + ":" + port);
        client = Consul.builder().withHostAndPort(HostAndPort.fromParts(host, port)).build();
    }


    @Override
    public void add(ServiceInfo serviceInfo) {
        //String serviceName = "prometheus-etcd";
        //serviceId相当记录的唯一标识，如果id相同则会覆盖。
        //String serviceId = "etcd4";
        //// Check in with Consul (serviceId required only).
        //// Client will prepend "service:" for service level checks.
        //// Note that you need to continually check in before the TTL expires, otherwise your service's state will be marked as "critical".
        // ttl(10L)过期时间为10秒
        //Registration.RegCheck single = Registration.RegCheck.tcp("127.0.0.1:18080", 5).ttl(13L);
        //调整为ttl(13L);无ttl的方式
        //TCP+ Interval 的check 方式
        Registration.RegCheck single = Registration.RegCheck.tcp(serviceInfo.getCheckUrl(), serviceInfo.getCheckInterval());
        Registration service = ImmutableRegistration.builder()
                .id(serviceInfo.getId())
                .name(serviceInfo.getName())
                .address(serviceInfo.getIp())
                .port(serviceInfo.getPort())
                .check(single) // registers with a TTL of 3 seconds
                .tags(Collections.singletonList(serviceInfo.getTag()))
                .meta(Collections.singletonMap("version", "1.0"))
                .build();
        client.agentClient().register(service);
    }

    @Override
    public void delete(String serviceId) {
        client.agentClient().deregister(serviceId);
    }

    @Override
    public List<ServiceInfo> getAllHealthyServiceByServiceName(String serviceName) {
        List<ServiceInfo> list4ServiceInfo = new ArrayList<>();
        //
        HealthClient healthClient = client.healthClient();
        // Discover only "passing" nodes
        //List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances("prometheus-etcd").getResponse();
        //如果在服务注册的时候使用的是http方式，则getResponse中会带有大量的响应信息-output。使用传输的数据变大
        //因此推荐使用TCP方式进行服务的检查
        //目前的功能主要是consul作为prometheus的注册发现,jmx_exporter输出的信息比较多,所以推荐tcp注册方式
        //https://github.com/prometheus/jmx_exporter
        List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances(serviceName).getResponse();
        for (ServiceHealth item : nodes) {
            log.info(item.getService().toString());
            log.info(item.toString());
            com.orbitz.consul.model.health.Service item4Service = item.getService();
            list4ServiceInfo.add(ServiceInfo.newBuilder()
                    .name(item4Service.getService())
                    .ip(item4Service.getAddress())
                    .port(item4Service.getPort())
                    .build());
        }

        return list4ServiceInfo;
    }

    @Override
    public List<ServiceInfo> getAllHealthyServiceByServiceTag(String serviceTag) {
        List<ServiceInfo> list4ServiceInfo = new ArrayList<>();
        Map<String, com.orbitz.consul.model.health.Service> serviceMap = client.agentClient().getServices();
        if (serviceMap.isEmpty()) {
            return list4ServiceInfo;
        }
        List<String> list4ServiceId4Pass = new ArrayList<>();
        Map<String, HealthCheck> serviceHealthCheckMap = client.agentClient().getChecks();
        for (Map.Entry<String, HealthCheck> entry : serviceHealthCheckMap.entrySet()) {
            HealthCheck item4HealthCheck = entry.getValue();
            String serviceName = item4HealthCheck.getServiceName().get();
            String serviceId = item4HealthCheck.getServiceId().get();
            String status = item4HealthCheck.getStatus();
            //通过标签可以设置灰度识别，是否需要监控，监控的类型：M-JVM,M-REDIS,M-MYSQL
            if (!item4HealthCheck.getServiceTags().contains(serviceTag)) {
                continue;
            }
            //获取健康状态值  PASSING：正常  WARNING  CRITICAL  UNKNOWN：不正常
            if (!State.PASS.getName().equals(item4HealthCheck.getStatus())) {
                continue;
            }
            log.info("服务名称 :{}/服务ID:{},健康状态值：{}", serviceName, serviceId, status);
            list4ServiceId4Pass.add(serviceId);
        }
        if (list4ServiceId4Pass.isEmpty()) {
            return list4ServiceInfo;
        }
        for (Map.Entry<String, com.orbitz.consul.model.health.Service> entry : serviceMap.entrySet()) {
            if (!list4ServiceId4Pass.contains(entry.getKey())) {
                continue;
            }
            com.orbitz.consul.model.health.Service item4Service = entry.getValue();
            list4ServiceInfo.add(ServiceInfo.newBuilder()
                    .name(item4Service.getService())
                    .ip(item4Service.getAddress())
                    .port(item4Service.getPort())
                    .build());
        }
        return list4ServiceInfo;
    }

    @Override
    public void putValue4Upstream(ServiceInfo serviceInfo) {
        UpstreamInfo upstreamInfo=new UpstreamInfo();
        upstreamInfo.setWeight(10);
        upstreamInfo.setMaxFails(2);
        upstreamInfo.setFail_timeout(10);
        upstreamInfo.setDown(0);
        String value=FastJsonUtil.serialize(upstreamInfo);
        String key= getKeyByServiceInfo(serviceInfo);
        KeyValueClient kvClient = client.keyValueClient();
        kvClient.putValue(key,value);
    }

    @Override
    public void deleteValue4Upstream(ServiceInfo serviceInfo) {
        String key= getKeyByServiceInfo(serviceInfo);
        KeyValueClient kvClient = client.keyValueClient();
        kvClient.deleteKey(key);
    }

    private String getKeyByServiceInfo(ServiceInfo serviceInfo) {
        return String.format("/upstreams/%s/%s:%s",serviceInfo.getName(),serviceInfo.getIp(),serviceInfo.getPort());
    }
}
