package com.webank.ppc.iss.job;

import com.webank.ppc.iss.common.EventEnum;
import com.webank.ppc.iss.config.DataWriterService;
import com.webank.ppc.iss.message.ChainQueueSet;
import com.webank.ppc.iss.service.PpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@ConditionalOnProperty(name = "service.data-algorithm-event-enabled", havingValue = "true")
@Slf4j
public class RemoveScheduledJob {

    @Autowired
    private PpcService ppcService;

    @Autowired
    DataWriterService dataWriterService;

    @Scheduled(initialDelayString = "${service.schedule-initial-delay}", fixedDelayString = "${service.schedule-fixed-delay}")
    public void doRemoveData() throws Exception {
        if (!PpcService.chainRemoveQueueSets.isEmpty()) {
            log.info("chainRemoveQueueSets is not empty");
            ChainQueueSet chainQueueSet = PpcService.chainRemoveQueueSets.poll();
            log.info("isLocker: {}, doRemoveData, {}, {}, {}", dataWriterService.isLocker(), chainQueueSet.getTableName(), chainQueueSet.getMetaKey(), chainQueueSet.getBlockNumber());
            if (dataWriterService.isLocker()) {
                removeChainSetFromDatabase(chainQueueSet.getTableName(), chainQueueSet.getMetaKey(), chainQueueSet.getBlockNumber());
            }
        }
    }

    private void removeChainSetFromDatabase(String tableName, String metaKey, BigInteger blockNumber) {
        log.info("removeChainSetFromDatabase: tableName: {}, metaKey:{}, blockNumber:{}", tableName, metaKey, blockNumber);
        ppcService.removeMetaDatabase(tableName, metaKey, blockNumber);
        ppcService.setEventStatus(EventEnum.REMOVE.getValue(), blockNumber.longValue());
    }


}
