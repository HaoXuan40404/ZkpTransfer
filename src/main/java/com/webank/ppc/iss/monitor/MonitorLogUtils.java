/** Copyright (C) @2014-2022 Webank */
package com.webank.ppc.iss.monitor;

import static java.time.LocalDateTime.now;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.webank.ppc.iss.message.PpcMonitorBody;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorLogUtils extends HashMap<String, Object> {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(MonitorLogUtils.class);
    private static final Logger MONITOR = LoggerFactory.getLogger("monitor");

    public static void log(PpcMonitorBody ppcMonitorBody) {
        String content = "";
        try {
            content = objectMapper.writeValueAsString(ppcMonitorBody);
        } catch (JsonProcessingException e) {
            MONITOR.error("{}", "encode monitor object to json failed");
        }
        MONITOR.info("{}", content);
    }

    /**
     * Adds a useful api for printing monitor log.
     *
     * @param start
     * @param monitorErrorType
     */
    public static void printMonitorLog(
            String methodName, LocalDateTime start, MonitorErrorType monitorErrorType) {
        printMonitorLog(methodName, start, monitorErrorType, -1);
    }

    /**
     * Adds a useful api for printing monitor log.
     *
     * @param start
     * @param monitorErrorType
     */
    public static void printMonitorLog(
            String bizSeq,
            String sysSeq,
            String methodName,
            LocalDateTime start,
            MonitorErrorType monitorErrorType) {
        printMonitorLog(bizSeq, sysSeq, methodName, start, monitorErrorType, -1);
    }

    /**
     * Adds a useful api for printing monitor log.
     *
     * @param start
     * @param monitorErrorType
     */
    public static void printMonitorLog(
            String methodName,
            LocalDateTime start,
            MonitorErrorType monitorErrorType,
            int timeout) {
        printMonitorLog("", "", methodName, start, monitorErrorType, timeout);
    }

    /**
     * Adds a useful api for printing monitor log.
     *
     * @param start
     * @param monitorErrorType
     */
    public static void printMonitorLog(
            String bizSeq,
            String sysSeq,
            String methodName,
            LocalDateTime start,
            MonitorErrorType monitorErrorType,
            int timeout) {
        LocalDateTime end = now();
        long elapsedTime = Duration.between(start, end).toMillis();

        PpcMonitorBody ppcsMonitorBody = new PpcMonitorBody();
        ppcsMonitorBody.setCode(methodName);
        ppcsMonitorBody.setCostTime(String.valueOf(elapsedTime));
        ppcsMonitorBody.setResCode(String.valueOf(monitorErrorType.code()));
        ppcsMonitorBody.setBizSeq(bizSeq);
        ppcsMonitorBody.setSysSeq(sysSeq);

        MonitorLogUtils.log(ppcsMonitorBody);

        if (timeout > 0 && elapsedTime >= timeout) {
            logger.error(
                    "超时操作, 接口: {}, 耗时: {}, 超时时间: {}, bizSeq: {}, sysSeq: {}",
                    methodName,
                    elapsedTime,
                    timeout,
                    bizSeq,
                    sysSeq);
        }
    }

    /**
     * Sets methodName.
     *
     * @param methodName the methodName
     */
    public void setMethodName(String methodName) {
        put("code", methodName);
    }
    /**
     * Sets src biz seq.
     *
     * @param bizSeq the src biz seq
     */
    public void setBizSeq(String bizSeq) {
        put("biz_seq", bizSeq);
    }

    /**
     * Sets src sys seq.
     *
     * @param sysSeq the src sys seq
     */
    public void setSysSeq(String sysSeq) {
        put("sys_seq", sysSeq);
    }

    /**
     * Sets cost time.
     *
     * @param costTime the cost time
     */
    public void setCostTime(Long costTime) {
        put("cost_time", costTime);
    }

    /**
     * Sets res code.
     *
     * @param resCode the res code
     */
    public void setResCode(MonitorErrorType resCode) {
        put("res_code", resCode.code());
    }
}
