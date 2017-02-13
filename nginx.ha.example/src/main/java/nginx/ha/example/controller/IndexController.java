package nginx.ha.example.controller;

import nginx.ha.example.Util.DateUtil;
import nginx.ha.example.Util.ServerUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/2/10.
 */
@Controller
public class IndexController {
    @RequestMapping("/")
    @ResponseBody
    public String index() {
        StringBuilder sb=new StringBuilder();
        sb.append("测试：利用 Nginx 负载均衡实现 Web 服务器更新不影响访问|");
        sb.append("测试：spring boot 是否可以优雅退出|");
        sb.append(String.format("这是网站:%s|网站端口:%s|", ServerUtil.value,ServerUtil.port));
        sb.append(String.format("当前时间:%s", DateUtil.currentTime()));
        return sb.toString();
    }
}
