package com.webank.wedpr.zktransfer.job;

import com.webank.wedpr.crypto.zkp.NativeInterface;
import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.entity.CommitmentEntity;
import com.webank.wedpr.zktransfer.repository.CommitmentRepository;
import com.webank.wedpr.zktransfer.service.AESUtils;
import com.webank.wedpr.zktransfer.service.ChainService;
import com.webank.wedpr.zktransfer.utils.KeyDriveFunction;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Component
public class SyncCommitment {

    @Autowired private NativeInterface nativeInterface;

    @Autowired private byte[] servicePrivateKey;

    @Autowired
    private ChainService chainService;

    @Autowired
    private CommitmentRepository commitmentRepository;

    // TODO: 稳健性 服务启动时启动 扫描一次所有的CM
    @Scheduled(fixedRate = 1000) // Runs every second
    public void syncCommitments() throws NoSuchAlgorithmException, WedprException {
        log.info("Syncing commitments...");
        while (true) {
            // 读取CommitmentRepository中Commitment index的最大值
            Optional<CommitmentEntity> maxIndexCommitmentOpt = commitmentRepository.findMaxIndexCommitment();
            int currentIndex = 0;
            if(maxIndexCommitmentOpt.isPresent())
            {
                currentIndex = maxIndexCommitmentOpt.get().getIndex();
            }
            log.info("Current index: {}", currentIndex);
            byte[] indexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, currentIndex);
            byte[] viewKey = nativeInterface.computeViewkey(indexBlinding).expectNoError().viewkey;
            currentIndex++;
            byte[] valueCipher = chainService.getCipherByViewKey(viewKey);
            // 未查询到 表示已经同步完毕
            if(valueCipher.length == 0) {
                log.info("No more commitments to sync. index = {}", currentIndex);
                break;
            }
            // 解密失败异常中断 不是自己的viewKey
            byte[] valueBytes = AESUtils.decrypt(valueCipher, servicePrivateKey);

            // Convert valueBytes to int
            int value = ByteBuffer.wrap(valueBytes).getInt();
            // 计算生成commitment
            byte[] commitment = nativeInterface.computeCommitment(value, indexBlinding).expectNoError().commitment;
            int commitmentStatus = chainService.getCommitmentStatus(commitment);
            String commitmentStr = Hex.toHexString(commitment);

            // 根据commitment查询db
            Optional<CommitmentEntity> commitmentEntityOpt = commitmentRepository.findByCommitment(commitmentStr);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (commitmentEntityOpt.isPresent()) {
                CommitmentEntity commitmentEntity = commitmentEntityOpt.get();
                if(commitmentEntity.getStatus() != commitmentStatus) {
                    log.info("Update commitment status. commitment = {}, status = {}", commitmentStr, commitmentStatus);
                    commitmentEntity.setStatus(commitmentStatus);
                    commitmentEntity.setUpdateTime(timestamp);
                    commitmentRepository.save(commitmentEntity);
                }
                
            } else {
                CommitmentEntity commitmentEntity = new CommitmentEntity();
                commitmentEntity.setCommitment(commitmentStr);
                commitmentEntity.setStatus(commitmentStatus);
                commitmentEntity.setIndex(currentIndex);
                commitmentEntity.setUpdateTime(timestamp);
                commitmentRepository.save(commitmentEntity);
            }


            
        }
    }
  
}
