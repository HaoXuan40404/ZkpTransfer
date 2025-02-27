package com.webank.ppc.iss.config;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;


/**
 * @author asher
 * @date 2024/6/25
 */
@Component
@Slf4j
public class DataWriterService {

    //    private boolean isLocker;
    SimpleLock simpleLock;

    @Autowired
    LockProvider lockProvider;

    @Autowired
    ShedConfig shedConfig;


//    public DataWriterService() {
//        this.isLocker = false;
//    }

//    @Async("asyncExecutorShedlock")
//    @Scheduled(cron = "${app.jobQueue.cron}")
//    @SchedulerLock(name = "getPpcLock", lockAtLeastFor = "${app.shedlock.lockAtLeastFor}", lockAtMostFor = "${app.shedlock.lockAtMostFor}")
//    public void processJobQueue() {
//        log.info("get lock succeed!");
//        this.isLocker = true;
//    }

    @Async("asyncExecutorShedlock")
    @Scheduled(cron = "${app.jobQueue.cron}")
//    @SchedulerLock(name = "getPpcLock", lockAtLeastFor = "${app.shedlock.lockAtLeastFor}", lockAtMostFor = "${app.shedlock.lockAtMostFor}")
    public void processJobQueue() {
        // 尝试获取锁
        if (simpleLock == null) {
            log.debug("simpleLock is null, try lock");
            Optional<SimpleLock> trySimpleLock = tryAcquireLockWithoutExp(lockProvider, "getPpcLock", shedConfig.getLockAtMostFor(), shedConfig.getLockAtLeastFor());
            if (trySimpleLock.isPresent()) {
                log.debug("try lock succeed!");
                simpleLock = trySimpleLock.get();
            } else {
                log.info("simpleLock try lock failed");
            }
        } else {
            Optional<SimpleLock> trySimpleLock = simpleLock.extend(Duration.parse(shedConfig.getLockAtMostFor()),
                    Duration.parse(shedConfig.getLockAtMostFor()));
            if (trySimpleLock.isPresent()) {
                log.debug("simpleLock extend succeed!");
                simpleLock = trySimpleLock.get();
            } else {
                log.info("simpleLock extend failed!");
                simpleLock = null;
            }
        }

    }

    public static Optional<SimpleLock> tryAcquireLockWithoutExp(
            LockProvider lockProvider, String lockName, String lockAtMost, String lockAtLeast) {
        // 获取锁
        try {
            return tryAcquireLock(lockProvider, lockName, lockAtMost, lockAtLeast);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<SimpleLock> tryAcquireLock(
            LockProvider lockProvider, String lockName, String lockAtMost, String lockAtLeast)
            throws Exception {
        try {
            Optional<SimpleLock> optional =
                    lockProvider.lock(
                            new LockConfiguration(
                                    Instant.now(),
                                    lockName,
                                    Duration.parse(lockAtMost),
                                    Duration.parse(lockAtLeast)));

            if (optional.isPresent()) {
                log.info(
                        "获得分布式锁, lockName: {}, lockAtMost: {}, lockAtLeast: {}",
                        lockName,
                        lockAtMost,
                        lockAtLeast);
                return optional;
            }

            log.info(
                    "没有获取到分布式锁, lockName: {}, lockAtMost: {}, lockAtLeast: {}",
                    lockName,
                    lockAtMost,
                    lockAtLeast);

            return optional;
        } catch (Exception e) {
            log.error("获取分布式锁异常, lockName: {}, 异常: ", lockName, e);
            throw new RuntimeException(e);
        }
    }

    public boolean isLocker() {
        return simpleLock != null;
    }

}
