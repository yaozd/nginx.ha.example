package nginx.ha.example.controller;

import nginx.ha.example.Application;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/2/10.
 */
@Controller
@RequestMapping("/endpoint/")
public class EndPointController {
    @RequestMapping("shutdown")
    public void shutdown() {
        //通过浏览器的方式进行关闭
        //todo 会产生一个死循环，因为当前请求就是一个http的请求
        //解决方法：可以把Application.ctx.close()放在一个单独的线程，请求做成一个异步请求就可以了。
        Application.ctx.close(); // I'm saving the context returned by spring boot in a static variable inside my main class
    }
}
