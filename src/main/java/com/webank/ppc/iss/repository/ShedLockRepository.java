package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.ShedLockEntity;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface ShedLockRepository extends CrudRepository<ShedLockEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select shedlockEntity from ShedLockEntity shedlockEntity where shedlockEntity.name = :name")
    Optional<ShedLockEntity> findAndAcquireLock(String name);

    @Transactional
    void deleteByName(String name);
}