package nginx.ha.example;

import nginx.ha.example.Util.ArgumentsUtil;
import nginx.ha.example.Util.ServerUtil;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public static ConfigurableApplicationContext ctx=null;
    public static void main(String[] args) {
        ServerUtil.value = ArgumentsUtil.getArgsValue(args,"--serviceUtil.value=");
        ServerUtil.port = ArgumentsUtil.getArgsValue(args,"--server.port=");
        Application.ctx=SpringApplication.run(Application.class, args);
    }
    // 参考：
    // Shut down embedded servlet container gracefully
    //https://github.com/spring-projects/spring-boot/issues/4657
    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }

    @Bean
    public EmbeddedServletContainerCustomizer tomcatCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                if (container instanceof TomcatEmbeddedServletContainerFactory) {
                    ((TomcatEmbeddedServletContainerFactory) container)
                            .addConnectorCustomizers(gracefulShutdown());
                }
            }
        };
    }

    private static class GracefulShutdown implements TomcatConnectorCustomizer,
            ApplicationListener<ContextClosedEvent> {

        private static final Logger log = LoggerFactory.getLogger(GracefulShutdown.class);

        private volatile Connector connector;

        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            this.connector.pause();
            Executor executor = this.connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                try {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                        log.warn("Tomcat thread pool did not shut down gracefully within "
                                + "30 seconds. Proceeding with forceful shutdown");
                    }
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
