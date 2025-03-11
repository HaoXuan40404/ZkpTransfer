package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.WedprException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;

/**
 * AES encryption and decryption utility class.
 */
public class AESUtils {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";

    public static byte[] intToBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4); // 分配 4 字节的缓冲区（int 占 4 字节）
        buffer.putInt(value);                       // 写入 int 值
        return buffer.array();                      // 返回字节数组
    }

    public static int bytesToInt(byte[] value) {
        return ByteBuffer.wrap(value).getInt();
    }

    private static SecretKeySpec getSecretKey(byte[] key) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(key);
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key) throws WedprException {
        byte[] iv = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        byte[] encryptedBytes;
        try {
            encryptedBytes = encrypt(plaintext, key, iv);
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
                | BadPaddingException | InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            throw new WedprException("Encrypt failed");
        }
        byte[] message = new byte[12 + encryptedBytes.length];
        System.arraycopy(iv, 0, message, 0, 12);
        System.arraycopy(encryptedBytes, 0, message, 12, encryptedBytes.length);
        return message;
    }
    
    public static byte[] decrypt(byte[] ciphertext, byte[] key) throws WedprException {
        if (ciphertext.length < 12 + 16) {
            throw new IllegalArgumentException("Invalid ciphertext length");
        }
        byte[] iv = new byte[12];
        System.arraycopy(ciphertext, 0, iv, 0, 12);
        byte[] encryptedBytes = new byte[ciphertext.length - 12];
        System.arraycopy(ciphertext, 12, encryptedBytes, 0, encryptedBytes.length);
        try {
            return decrypt(encryptedBytes, key, iv);
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
                | BadPaddingException | InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            throw new WedprException("Decrypt failed");
        }
    }

    public static byte[] encrypt(byte[] contentBytes, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec params = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key), params);
        return cipher.doFinal(contentBytes);
    }
    
    public static byte[] decrypt(byte[] encryptedBytes, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec params = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key), params);
        return cipher.doFinal(encryptedBytes);
    }
}