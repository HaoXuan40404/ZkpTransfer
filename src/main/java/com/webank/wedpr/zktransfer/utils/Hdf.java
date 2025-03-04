package com.webank.wedpr.zktransfer.utils;

import java.security.NoSuchAlgorithmException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class Hdf {
    public static byte[] deriveKey(byte[] key, int index) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        ByteBuffer buffer = ByteBuffer.allocate(key.length + Integer.BYTES);
        buffer.put(key);
        buffer.putInt(index);
        return digest.digest(buffer.array());
    }
}
