package com.webank.ppc.iss.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author: caryliao
 * @date: 2022/2/18 10:38
 */
@Entity
@Data
@Table(name = "d_blockchain")
public class BlockChain {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    @Id
    private String eventType;

    private Long maxBlockNumber;

}
