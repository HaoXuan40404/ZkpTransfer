package com.webank.wedpr.zktransfer.repository;

import com.webank.wedpr.zktransfer.entity.CommitmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommitmentRepository extends JpaRepository<CommitmentEntity, String> {

    @Query("SELECT c FROM CommitmentEntity c WHERE c.index = (SELECT MAX(c2.index) FROM CommitmentEntity c2)")
    Optional<CommitmentEntity> findMaxIndexCommitment();

    @Query("SELECT SUM(c.value) FROM CommitmentEntity c WHERE c.status = ?1")
    Integer sumValuesByStatus(int status);

    Optional<CommitmentEntity> findByCommitment(String commitment);
}
