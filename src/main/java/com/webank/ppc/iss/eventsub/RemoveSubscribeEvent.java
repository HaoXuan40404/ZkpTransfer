package com.webank.ppc.iss.eventsub;

import com.webank.ppc.iss.config.ServiceConfig;
import com.webank.ppc.iss.contracts.PpcContractController;
import com.webank.ppc.iss.message.ChainQueueSet;
import com.webank.ppc.iss.service.PpcService;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.eventsub.EventSubStatus;
import org.fisco.bcos.sdk.v3.model.EventLog;


import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
public class RemoveSubscribeEvent implements EventSubCallback {

    private Client client;

    private ServiceConfig serviceConfig;

    private PpcService ppcService;


    public RemoveSubscribeEvent(Client client, ServiceConfig serviceConfig, PpcService ppcService) {
        this.client = client;
        this.serviceConfig = serviceConfig;
        this.ppcService = ppcService;
    }

    public transient Semaphore semaphore = new Semaphore(1, true);

    RemoveSubscribeEvent() {
        try {
            semaphore.acquire(1);
        } catch (InterruptedException e) {
            log.error("error :", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onReceiveLog(String eventId, int status, List<EventLog> logs) {
        log.info("RemoveSubscribeEvent event push Enter.");
        String str = "status in onReceiveLog : " + status;
        log.debug(str);
        semaphore.release();
        System.out.println("event push Enter.");
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
                    List<Object> list = abiCodec.decodeEvent(PpcContractController.ABI, "removeMetaEvent", eventLog);
                    log.info("decode event log content, " + list);
                    if (list.size() != 2) {
                        log.warn("decode list failed!");
                    }
                    String tableName = (String) list.get(0);
                    String metaKey = (String) list.get(1);
                    log.info("tableName:{}", tableName);
                    log.info("metaKey:{}", metaKey);
                    ChainQueueSet chainQueueSet = new ChainQueueSet();
                    chainQueueSet.setTableName(tableName);
                    chainQueueSet.setMetaKey(metaKey);
                    chainQueueSet.setBlockNumber(eventLog.getBlockNumber());
                    log.info("add chainUpdateQueueSets to queue");
                    ppcService.chainRemoveQueueSets.add(chainQueueSet);
                    // TODO: do auth api to async
//                    removeChainSetFromDatabase(tableName, metaKey, eventLog.getBlockNumber());
                } catch (Exception e) {
                    log.info("decode event log error, " + e.getMessage());
                }
            }
        }
    }

}
