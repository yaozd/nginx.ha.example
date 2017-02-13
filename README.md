# nginx.ha.example

-利用 Nginx 负载均衡实现Spring boot开发的Web 服务器更新不影响访问--nginx实现WEB高可用
```sh
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
```
nginx.conf
```sh
#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
	#---------------------------------------------------------------
	#name:t1.test.com
	upstream backend {
    server 127.0.0.1:9912 weight=1;
    server 127.0.0.1:9913 weight=4;
    server 127.0.0.1:9914 backup;
	}
	server {
    listen 80;
    server_name t1.test.com; 
	location / {
        root   html;
        index  index.html index.htm;
        proxy_pass  http://backend;
    }
	}
	#---------------------------------------------------------------
}
``` 
