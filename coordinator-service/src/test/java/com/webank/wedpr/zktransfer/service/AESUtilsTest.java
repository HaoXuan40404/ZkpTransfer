// package com.webank.wedpr.zktransfer.service;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;




// public class AESUtilsTest {

//     @Test
//     public void testEncryptDecrypt() throws Exception {
//         System.out.println("AESUtilsTest.testEncryptDecrypt start");
//         String plaintext = "Hello, World!";
//         String key = "mysecretkey12345";

//         String encrypted = AESUtils.encrypt(plaintext, key);
//         String decrypted = AESUtils.decrypt(encrypted, key);

//         assertEquals(plaintext, decrypted);
//         System.out.println("AESUtilsTest.testEncryptDecrypt: " + decrypted);
//     }

//     @Test
//     public void testDecryptWithInvalidKey() throws Exception {
//         String plaintext = "Hello, World!";
//         String key = "mysecretkey12345";
//         String invalidKey = "invalidkey12345";

//         String encrypted = AESUtils.encrypt(plaintext, key);

//         assertThrows(Exception.class, () -> {
//             AESUtils.decrypt(encrypted, invalidKey);
//         });
//     }

//     @Test
//     public void testDecryptWithInvalidCiphertext() {
//         String key = "mysecretkey12345";
//         String invalidCiphertext = "invalidciphertext";

//         assertThrows(IllegalArgumentException.class, () -> {
//             AESUtils.decrypt(invalidCiphertext, key);
//         });
//     }
// }