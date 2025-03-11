//package com.webank.wedpr.zktransfer.service;
//
//import java.nio.ByteBuffer;
//import java.security.NoSuchAlgorithmException;
//import java.security.Timestamp;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//import com.webank.wedpr.crypto.zkp.NativeInterface;
//import com.webank.wedpr.crypto.zkp.WedprException;
//import com.webank.wedpr.zktransfer.utils.Commitment;
//import com.webank.wedpr.zktransfer.utils.CommitmentStatus;
//import com.webank.wedpr.zktransfer.utils.CustomerKeyStatus;
//import com.webank.wedpr.zktransfer.utils.Hdf;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class KeyManagerService {
//
//    @Autowired
//    private NativeInterface nativeInterface;
//
//    @Autowired private ChainService chainService;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private static byte[] generateViewKeySecret(byte[] customerSecretKey, int index)
//    {
//        byte[] result = null;
//        try {
//            result = Hdf.deriveKey(customerSecretKey, index);
//        } catch (NoSuchAlgorithmException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    private byte[] generateViewKeyPublic(byte[] secret) throws WedprException {
//        return nativeInterface.computeViewkey(secret).expectNoError().viewkey;
//    }
//
//    // TODO: 增加链上批量查询接口
//
//    // 1. 根据私钥和递增的index查询链上cipher 如果不存在则直接返回不存在
//    // 2. 使用sk解密cipher 得到金额v 计算Commitment 去链上查询得到Commitment状态
//    // 3. 如果Commitment状态为不存在则报错 否则将状态和Commitment status返回
//    public Commitment queryCommitment(byte[] customerSecretKey, int index) throws WedprException {
//        // 1. 根据计算index r = H(sk,index)
//        byte[] commitmentSecret = generateViewKeySecret(customerSecretKey, index);
//        // vk = rG
//        byte[] viewKey = generateViewKeyPublic(commitmentSecret);
//        // 2. 查询链上cipher
//        byte[] cipher = chainService.getCipherByViewKey(viewKey);
//        if(cipher == null) {
//            // 不存在
//            return new Commitment(CommitmentStatus.NotExist, 0, null);
//        }
//        // 3. 解密cipher
//        byte[] decryptedValue = AESUtils.decrypt(cipher, viewKey);
//        int value = ByteBuffer.wrap(decryptedValue).getInt();
//        byte[] commitmentBytes = nativeInterface.computeCommitment(value, commitmentSecret).expectNoError().commitment;
//        // 4. 查询链上Commitment状态
//        int status = chainService.getCommitmentStatus(commitmentBytes);
//        CommitmentStatus commitmentStatus = CommitmentStatus.values()[status];
//        if(commitmentStatus == CommitmentStatus.NotExist) {
//            // 不存在
//            throw new WedprException("Commitment not exist");
//        }
//        Commitment commitment = new Commitment(commitmentStatus, value, commitmentBytes);
//        return commitment;
//    }
//
//
//
//    public void addAccountKey(String address, String keyStr) throws WedprException {
//        AccountKey accountKey =  new AccountKey();
//        accountKey.setAddress(address);
//        accountKey.setCipherKey(keyStr);
//        accountKey.setStatus(CustomerKeyStatus.Exist.getValue());
//        java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
//        accountKey.setCreateTime(timestamp);
//        accountKey.setUpdateTime(timestamp);
//        accountKeyRepository.save(accountKey);
//        try {
//            createCommitmentTableIfNotExists(address);
//        } catch (SQLException e) {
//            throw new WedprException("Create commitment table failed");
//        }
//    }
//
//    // jdbc无法支持根据entity动态创建表
//    public void createCommitmentTableIfNotExists(String accountAddress) throws SQLException {
//        String tableName = "t_" + accountAddress + "_commitment";
//        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
//                "commitment VARCHAR(256) NOT NULL COMMENT '密文承诺', " +
//                "`index` INT NOT NULL UNIQUE COMMENT '派生索引 自增', " +
//                "value INT NOT NULL COMMENT '承诺金额', " +
//                "secret VARCHAR(256) NOT NULL COMMENT '承诺私钥', " +
//                "view_key VARCHAR(256) NOT NULL COMMENT '查看公钥 secret对应的公钥', " +
//                "status INT NOT NULL COMMENT '状态 1未花费 0已花费', " +
//                "create_time TIMESTAMP NOT NULL COMMENT '创建的时间', " +
//                "update_time TIMESTAMP NOT NULL COMMENT '修改的时间', " +
//                "PRIMARY KEY (commitment)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='密文承诺信息表';";
//        jdbcTemplate.execute(createTableSQL);
//    }
//
//
//    // 根据传入的accountAddress查询对应的commitment表中status为1的value总和
//    public int queryTotalValue(String accountAddress) {
//        String tableName = "t_" + accountAddress + "_commitment";
//        String querySQL = "SELECT SUM(value) FROM " + tableName + " WHERE status = 1";
//        return jdbcTemplate.queryForObject(querySQL, Integer.class);
//    }
//
//    // 根据accountAddress查询AccountKeyRepository中的Status
//    public int queryAccountKeyStatus(String accountAddress) {
//        AccountKey accountKey = accountKeyRepository.findById(accountAddress).orElse(null);
//        if(accountKey == null) {
//            return CustomerKeyStatus.NotExist.getValue();
//        }
//        return accountKey.getStatus();
//    }
//
//    // 获取commitment表中最大的index
//    public int queryMaxIndex(String accountAddress) {
//        String tableName = "t_" + accountAddress + "_commitment";
//        String querySQL = "SELECT MAX(`index`) FROM " + tableName;
//        return jdbcTemplate.queryForObject(querySQL, Integer.class);
//    }
//}
