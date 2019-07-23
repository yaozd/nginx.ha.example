package nginx.ha.example;

import nginx.ha.example.Util.ArgumentsUtil;
import nginx.ha.example.Util.ServerUtil;
import nginx.ha.example.config.ApplicationEventListener;
import nginx.ha.example.config.GracefulShutdownTomcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Administrator on 2017/2/10.
 */
@SpringBootApplication
@ComponentScan("nginx.ha.example")
public class Application {
    /**
     * 测试：利用 Nginx 负载均衡实现 Web 服务器更新不影响访问
     * http://www.cnblogs.com/mafly/p/upstream_backup.html
     * 测试：spring boot 是否可以优雅退出--kill application name
     * --serviceUtil.value=AAAAA
     * --server.port=9912
     * java -jar nginx.ha.example-1.0-SNAPSHOT-exec.jar --serviceUtil.value=AAAAA --server.port=9901
     *
     * @param args --serviceUtil.value=AAAAA --server.port=9912
     */
    //todo 如果不需要http形式的优雅退出，则不需要设置ConfigurableApplicationContext ctx这个静态变量，没有用还占资源
    public static ConfigurableApplicationContext ctx = null;

    public static void main(String[] args) {
        ServerUtil.value = ArgumentsUtil.getArgsValue(args, "--serviceUtil.value=");
        ServerUtil.port = ArgumentsUtil.getArgsValue(args, "--server.port=");
        SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new ApplicationEventListener());
        Application.ctx = app.run(args);
    }

    @Autowired
    private GracefulShutdownTomcat gracefulShutdownTomcat;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers(gracefulShutdownTomcat);
        return tomcat;
    }
}
