package com.webank.wedpr.zktransfer.repository;

import com.webank.wedpr.zktransfer.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    // Custom query methods can be added here if needed
}