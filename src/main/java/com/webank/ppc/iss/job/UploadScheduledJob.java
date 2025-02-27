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
public class UploadScheduledJob {

    @Autowired
    private PpcService ppcService;

    @Autowired
    DataWriterService dataWriterService;

    @Scheduled(initialDelayString = "${service.schedule-initial-delay}", fixedDelayString = "${service.schedule-fixed-delay}")
    public void doUploadData() throws Exception {
        if (!PpcService.chainUploadQueueSets.isEmpty()) {
            log.info("chainUploadQueueSets is not empty");
            ChainQueueSet chainQueueSet = PpcService.chainUploadQueueSets.poll();
            log.info("isLocker: {}, doUploadData, {}, {}, {}, {}", dataWriterService.isLocker(), chainQueueSet.getTableName(), chainQueueSet.getMetaKey(), chainQueueSet.getMetaValue(), chainQueueSet.getBlockNumber());
            if (dataWriterService.isLocker()) {
                uploadChainSetToDatabase(chainQueueSet.getTableName(), chainQueueSet.getMetaKey(), chainQueueSet.getMetaValue(), chainQueueSet.getBlockNumber());
            }
        }
    }

    private void uploadChainSetToDatabase(String tableName, String metaKey, String metaValue, BigInteger blockNumber) {
        log.info("uploadChainSetToDatabase: tableName: {}, metaKey:{}, metaValue:{}, blockNumber:{}", tableName, metaKey, metaValue, blockNumber);
        ppcService.uploadMetaDatabase(tableName, metaKey, metaValue, blockNumber);
        ppcService.setEventStatus(EventEnum.UPLOAD.getValue(), blockNumber.longValue());

    }


}
