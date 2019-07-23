package com.yzd.consul.common.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceInfo {
    //serviceId相当记录的唯一标识，如果id相同则会覆盖。
    private String id;
    private String name;
    private String ip;
    private Integer port;
    private String tag;
    private String checkUrl;
    private Long checkInterval;

    public String getId() {
        return this.name + "-" + this.ip;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private ServiceInfo(Builder builder) {
        setName(builder.name);
        setIp(builder.ip);
        setPort(builder.port);
        setTag(builder.tag);
        setCheckUrl(builder.checkUrl);
        setCheckInterval(builder.checkInterval);
    }

    public static final class Builder {
        private String name;
        private String ip;
        private Integer port;
        private String tag;
        private String checkUrl;
        private Long checkInterval;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder ip(String val) {
            ip = val;
            return this;
        }

        public Builder port(Integer val) {
            port = val;
            return this;
        }

        public Builder tag(String val) {
            tag = val;
            return this;
        }

        public Builder checkUrl(String val) {
            checkUrl = val;
            return this;
        }

        public Builder checkInterval(Long val) {
            checkInterval = val;
            return this;
        }

        public ServiceInfo build() {
            return new ServiceInfo(this);
        }
    }
}
