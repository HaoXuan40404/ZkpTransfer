package com.webank.ppc.iss.eventsub;

import com.webank.ppc.iss.common.PpcCommonUtils;
import com.webank.ppc.iss.config.ServiceConfig;
import com.webank.ppc.iss.contracts.PpcContractController;
import com.webank.ppc.iss.message.ChainJobEventQueueSet;
import com.webank.ppc.iss.message.UploadJobEventHttpRequest;
import com.webank.ppc.iss.message.UploadJobEventHttpResponse;
import com.webank.ppc.iss.service.PpcService;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.eventsub.EventSubStatus;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
public class JobSubscribeEvent implements EventSubCallback {

    private Client client;

    private ServiceConfig serviceConfig;

    public JobSubscribeEvent(Client client, ServiceConfig serviceConfig) {
        this.client = client;
        this.serviceConfig = serviceConfig;
    }

    public transient Semaphore semaphore = new Semaphore(1, true);

    JobSubscribeEvent() {
        try {
            semaphore.acquire(1);
        } catch (InterruptedException e) {
            log.error("error :", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onReceiveLog(String eventId, int status, List<EventLog> logs) {
        log.info("收到任务event, 状态:{}", status);
        semaphore.release();
        ContractCodec abiCodec = new ContractCodec(client.getCryptoSuite(), false);
        if (status == EventSubStatus.PUSH_COMPLETED.getStatus()) {
            System.out.println("event push completed.");
        }
        if (logs != null) {
            for (EventLog eventLog : logs) {
                try {
                    log.info(
                            "status in onReceiveLog : "
                                    + status
                                    + ",blockNumber:"
                                    + eventLog.getBlockNumber()
                                    + ",txIndex:"
                                    + eventLog.getTransactionIndex()
                                    + ",data:"
                                    + eventLog.getData());
                    List<Object> list = abiCodec.decodeEvent(PpcContractController.ABI, "delegateJobEvent", eventLog);
                    log.info("解码的event log内容: {}", list);
                    if (list.size() != 2) {
                        log.warn("解码event log格式失败!");
                        continue;
                    }
                    String jobId = (String) list.get(0);
                    String jobEvent = (String) list.get(1);
                    log.info("收到链上的任务event, jobId:{}, jobEvent:{}", jobId, jobEvent);
                    // job任务先进内存队列
                    ChainJobEventQueueSet chainJobEventQueueSet = new ChainJobEventQueueSet();
                    chainJobEventQueueSet.setJobId(jobId);
                    chainJobEventQueueSet.setJobEvent(jobEvent);
                    chainJobEventQueueSet.setBlockNumber(eventLog.getBlockNumber());
                    PpcService.chainJobEventQueueSets.add(chainJobEventQueueSet);
                } catch (Exception e) {
                    log.error("解码event log 错误：{}", e.getMessage(), e);
                }
            }
        }
    }
}
