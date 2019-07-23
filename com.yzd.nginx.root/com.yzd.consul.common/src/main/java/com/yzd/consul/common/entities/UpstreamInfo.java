package com.yzd.consul.common.entities;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * nginx upstreams配置信息
 */
@Data
@NoArgsConstructor
public class UpstreamInfo {
    //{"weight":10, "max_fails":2, "fail_timeout":10, "down":0}
    @JSONField(name = "weight")
    private Integer weight;
    @JSONField(name = "max_fails")
    private Integer maxFails;
    @JSONField(name = "fail_timeout")
    private Integer fail_timeout;
    @JSONField(name = "down")
    private Integer down;

}
