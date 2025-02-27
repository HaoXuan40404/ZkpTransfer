package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author asher
 * @date 2024/3/12
 */
@Data
public class PpcMonitorBody {
    private String code;

    @JsonProperty("cost_time")
    private String costTime;

    @JsonProperty("res_code")
    private String resCode;

    @JsonProperty("biz_seq")
    private String bizSeq;

    @JsonProperty("sys_seq")
    private String sysSeq;

    @JsonProperty("ext")
    private String ext;
}
