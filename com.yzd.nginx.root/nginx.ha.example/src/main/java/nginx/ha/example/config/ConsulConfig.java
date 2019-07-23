package nginx.ha.example.config;

import com.yzd.consul.common.entities.MonitorType;
import com.yzd.consul.common.entities.ServiceInfo;
import com.yzd.consul.common.utils.ConsulClientImpl;
import com.yzd.consul.common.utils.IConsulClientInf;
import com.yzd.consul.common.utils.LocalIpAddressUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsulConfig {
    private static String consulUrl;
    private static String name;
    private static String ip;
    private static Integer port;
    private static Long checkInterval = 3l;

    @Value("${info.consul.url}")
    public void setConsulUrl(String url) {
        ConsulConfig.consulUrl = url;
    }

    @Value("${info.app.name}")
    public void setName(String name) {
        ConsulConfig.name = name;
    }

    @Value("${server.port}")
    public void setPort(Integer port) {
        ConsulConfig.port = port;
    }

    private static ServiceInfo getServiceInfo() {
        ip = LocalIpAddressUtil.getLocalIp();
        return ServiceInfo.newBuilder()
                .name(name)
                .ip(ip)
                .port(port)
                .checkUrl(ip + ":" + port)
                .checkInterval(checkInterval)
                .tag(MonitorType.M_GATEWAY.name())
                .build();
    }

    /**
     * 注册服务
     *
     * @return
     */
    public static boolean register() {
        ServiceInfo serviceInfo = getServiceInfo();
        IConsulClientInf consulClientInf = new ConsulClientImpl(consulUrl);
        String result = consulClientInf.add(serviceInfo);
        if ("ok".equals(result)) {
            return true;
        }
        throw new IllegalStateException("Consul服务注册失败，result=" + result);
    }

    /**
     * 取消服务
     *
     * @return
     */
    public static boolean deregister() {
        ServiceInfo serviceInfo = getServiceInfo();
        IConsulClientInf consulClientInf = new ConsulClientImpl(consulUrl);
        String result = consulClientInf.delete(serviceInfo);
        if ("ok".equals(result)) {
            return true;
        }
        throw new IllegalStateException("Consul服务取消失败，result=" + result);
    }
}
