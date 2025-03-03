package com.webank.wedpr.zktransfer.service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author asher
 * @date 2024/3/1
 */
public class AESUtils {

    //    private static final String DEFAULT_AES_ALGORITHM = "AES";
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";

//    public static String defaultEncrypt(String plaintext, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), DEFAULT_AES_ALGORITHM);
//        Cipher cipher = Cipher.getInstance(DEFAULT_AES_ALGORITHM);
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
//        return Base64.getEncoder().encodeToString(encryptedBytes);
//    }
//
//    public static String defaultDecrypt(String ciphertext, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), DEFAULT_AES_ALGORITHM);
//        Cipher cipher = Cipher.getInstance(DEFAULT_AES_ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
//        return new String(decryptedBytes, StandardCharsets.UTF_8);
//    }

    public static String encrypt(String plaintext, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] iv = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        byte[] contentBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec params = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key), params);
        byte[] encryptData = cipher.doFinal(contentBytes);
        assert encryptData.length == contentBytes.length + 16;
        byte[] message = new byte[12 + contentBytes.length + 16];
        System.arraycopy(iv, 0, message, 0, 12);
        System.arraycopy(encryptData, 0, message, 12, encryptData.length);
        return Base64.getEncoder().encodeToString(message);
    }


    public static String decrypt(String ciphertext, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] content = Base64.getDecoder().decode(ciphertext);
        if (content.length < 12 + 16) {
            throw new IllegalArgumentException();
        }
        GCMParameterSpec params = new GCMParameterSpec(128, content, 0, 12);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key), params);
        byte[] decryptData = cipher.doFinal(content, 12, content.length - 12);
        return new String(decryptData, StandardCharsets.UTF_8);
    }


    private static SecretKeySpec getSecretKey(String key) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        // 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes(StandardCharsets.UTF_8));
        kg.init(128, secureRandom);
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

}
