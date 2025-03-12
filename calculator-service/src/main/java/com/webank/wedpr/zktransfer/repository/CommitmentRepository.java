package com.webank.wedpr.zktransfer.repository;

import com.webank.wedpr.zktransfer.entity.CommitmentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommitmentRepository extends JpaRepository<CommitmentEntity, String> {

    @Query("SELECT c FROM CommitmentEntity c WHERE c.kdfIndex = (SELECT MAX(c2.kdfIndex) FROM CommitmentEntity c2)")
    Optional<CommitmentEntity> findMaxIndexCommitment();

    @Query("SELECT SUM(c.commitmentValue) FROM CommitmentEntity c WHERE c.status = ?1")
    Integer sumValuesByStatus(int status);

    Optional<CommitmentEntity> findByCommitment(String commitment);

    // 根据status查询CommitmentEntity的方法
    List<CommitmentEntity> findByStatus(int status);

    // 根据commitment和status修改对应commitment的status
    @Modifying
    @Transactional
    @Query("UPDATE CommitmentEntity c SET c.status = ?2 WHERE c.commitment = ?1")
    int updateStatusByCommitment(String commitment, int status);

}
