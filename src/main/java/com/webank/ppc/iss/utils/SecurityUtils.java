package com.webank.ppc.iss.utils;

import bsp.encrypt.EncryptUtil;
import bsp.encrypt.ParamType;
import bsp.encrypt.RSAUtils;

import static dm.jdbc.util.StringUtil.hexStringToBytes;

/**
 * @author asher
 * @date 2024/3/14
 */
public class SecurityUtils {

    public static String decrypt(String encryptedPwd, String appPrivateKey, String sysPublicKey) throws Exception {
        try {
            return EncryptUtil.decrypt(
                    ParamType.STRING,
                    sysPublicKey,
                    ParamType.STRING,
                    appPrivateKey,
                    ParamType.STRING,
                    encryptedPwd);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static String decrypt(String encryptedPwd, String appPrivateKey) throws Exception {
        try {
            return EncryptUtil.decrypt(appPrivateKey, encryptedPwd);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
