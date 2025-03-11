package com.webank.wedpr.zktransfer.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SyncCommitment {

    @Scheduled(fixedRate = 1000) // Runs every 60 seconds
    public void syncCommitments() {
        // 一秒一次 同步db的Commitment是否与链上保持最高
        log.info("Syncing commitments...");
        // 读取db中Commitment index的最大值

        // index + 1后与私钥派生新的viewKey
        // 查询链上新的viewKey是否存在，若存在就记录commitment直到重复到不存在

    }
}
