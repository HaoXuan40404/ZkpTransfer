package com.webank.ppc.iss.job;

import com.webank.ppc.iss.common.EventEnum;
import com.webank.ppc.iss.config.DataWriterService;
import com.webank.ppc.iss.config.ServiceConfig;
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
public class UpdateScheduledJob {

    @Autowired
    private PpcService ppcService;

    @Autowired
    ServiceConfig serviceConfig;

    @Autowired
    DataWriterService dataWriterService;


    @Scheduled(initialDelayString = "${service.schedule-initial-delay}", fixedDelayString = "${service.schedule-fixed-delay}")
    public void doUpdateData() throws Exception {
        if (!PpcService.chainUpdateQueueSets.isEmpty()) {
            log.info("chainUpdateQueueSets is not empty");
            ChainQueueSet chainQueueSet = PpcService.chainUpdateQueueSets.poll();
            log.info("isLocker: {}, doUpdateData, {}, {}, {}, {}", dataWriterService.isLocker(), chainQueueSet.getTableName(), chainQueueSet.getMetaKey(), chainQueueSet.getMetaValue(), chainQueueSet.getBlockNumber());
            if(dataWriterService.isLocker())
            {
                updateChainSetToDatabase(chainQueueSet.getTableName(), chainQueueSet.getMetaKey(), chainQueueSet.getMetaValue(), chainQueueSet.getBlockNumber());
            }
        }
    }

    private void updateChainSetToDatabase(String tableName, String metaKey, String metaValue, BigInteger blockNumber) {
        log.info("updateChainSetToDatabase: tableName: {}, metaKey:{}, metaValue:{}, blockNumber:{}", tableName, metaKey, metaValue, blockNumber);
        ppcService.uploadMetaDatabase(tableName, metaKey, metaValue, blockNumber);
        ppcService.setEventStatus(EventEnum.UPDATE.getValue(), blockNumber.longValue());
    }


}
