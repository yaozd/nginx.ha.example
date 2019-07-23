package nginx.ha.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

@Slf4j
//实现ApplicationListener接口：
public class ApplicationEventListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 在这里可以监听到Spring Boot的生命周期
        eventHandler(event);
    }

    private void eventHandler(ApplicationEvent event) {
        // 在这里可以监听到Spring Boot的生命周期
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            // 初始化环境变量
            System.out.println("初始化环境变量");
            return;
        }
        if (event instanceof ApplicationPreparedEvent) {
            // 初始化完成
            System.out.println("初始化完成");
            return;
        }
        if (event instanceof ContextRefreshedEvent) {
            // 应用刷新
            System.out.println("应用刷新");
            return;
        }
        if (event instanceof ApplicationReadyEvent) {
            // 应用已启动完成
            System.out.println("应用已启动完成");
            ConsulConfig.register();
            return;
        }
        if (event instanceof ContextStartedEvent) {
            // 应用启动，需要在代码动态添加监听器才可捕获
            System.out.println("应用启动，需要在代码动态添加监听器才可捕获");
            return;
        }
        if (event instanceof ContextStoppedEvent) {
            // 应用停止
            System.out.println("应用停止");
            return;
        }
        if (event instanceof ContextClosedEvent) {
            System.out.println("应用关闭");
            //调整”ConsulConfig.deregister()“在GracefulShutdownTomcat类中执行。
            //ConsulConfig.deregister();
            return;
        }
    }
}