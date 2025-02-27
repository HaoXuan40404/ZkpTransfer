package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.BlockChain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface BlockChainRepository
        extends PagingAndSortingRepository<BlockChain, Long>, JpaSpecificationExecutor<BlockChain> {

    BlockChain findFirstByEventType(String eventType);
}
