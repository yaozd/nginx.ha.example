package startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"com.yzd.consul.service"})
public class Application4Consul {
    /**
     * 测试地址：
     * http://127.0.0.1:27680/consul/getAllHealthyServiceByServiceTag?serviceTag=M_JVM
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application4Consul.class, args);
    }
}