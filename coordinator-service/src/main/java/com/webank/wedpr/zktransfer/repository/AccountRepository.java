package com.webank.wedpr.zktransfer.repository;

import com.webank.wedpr.zktransfer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    // Custom query methods can be added here if needed
}
