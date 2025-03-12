package com.webank.wedpr.zktransfer.repository;

import com.webank.wedpr.zktransfer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    // 根据name查询Account信息
    Optional<Account> findByName(String name);
}
