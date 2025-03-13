package com.webank.wedpr.zktransfer.repository;

import com.webank.wedpr.zktransfer.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    // Custom query methods can be added here if needed
    // 分页查询历史记录
    Page<TransactionHistory> findAll(Pageable pageable);
}