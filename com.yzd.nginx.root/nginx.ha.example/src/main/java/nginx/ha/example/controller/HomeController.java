package nginx.ha.example.controller;

import nginx.ha.example.Util.DateUtil;
import nginx.ha.example.Util.ServerUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/2/10.
 * 测试：spring boot 是否可以优雅退出--kill application name
 */
@Controller
@RequestMapping("/home/")
public class HomeController {
    @RequestMapping("sleep5")
    @ResponseBody
    public String sleep5() throws InterruptedException {
        System.out.println("/home/sleep5正在处理请求……");
        Thread.sleep(5 * 1000);
        System.out.println("/home/sleep5请求处理完成");
        return String.format("这是网站:%s|网站端口:%s|当前时间:%s|sleep5", ServerUtil.value, ServerUtil.port, DateUtil.currentTime());
    }

    @RequestMapping("sleep10")
    @ResponseBody
    public String sleep10() throws InterruptedException {
        System.out.println("/home/sleep10正在处理请求……");
        Thread.sleep(10 * 1000);
        System.out.println("/home/sleep10请求处理完成");
        return String.format("这是网站:%s|网站端口:%s|当前时间:%s|sleep10", ServerUtil.value, ServerUtil.port, DateUtil.currentTime());
    }

    @RequestMapping("sleep15")
    @ResponseBody
    public String sleep15() throws InterruptedException {
        System.out.println("/home/sleep15正在处理请求……");
        Thread.sleep(15 * 1000);
        System.out.println("/home/sleep15请求处理完成");
        return String.format("这是网站:%s|网站端口:%s|当前时间:%s|sleep15", ServerUtil.value, ServerUtil.port, DateUtil.currentTime());
    }
}
