//package com.webank.wedpr.zktransfer.service;
//
//import com.webank.wedpr.crypto.zkp.NativeInterface;
//import com.webank.wedpr.zktransfer.repository.AccountKeyRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//public class TransferService {
//
//    @Autowired
//    private NativeInterface nativeInterface;
//
//    @Autowired private ChainService chainService;
//
//    @Autowired
//    private AccountKeyRepository accountKeyRepository;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    // 并发相同的rG会导致链上写入失败 但是不会导致用户资产丢失
//
//    public void deposit(String accountAddress, long amount) {
//        // 查询db拿到用户密钥 和commitment最大的index
//
//        String customerKey = accountKeyRepository.findByAddress(accountAddress).get().toString();
//
//        // 使用银行账户approve amount给合约地址
//        // 生成新的commitment
//    }
//
//
//}
