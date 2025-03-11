package com.webank.wedpr.crypto.zkp;

//import com.webank.wedpr.crypto.zkp.NativeInterface;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class ZkpDemo {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static void main(String[] args) throws WedprException {
        NativeInterface zkp = new NativeInterface();

        // 测试知识证明（Knowledge Proof）
        testKnowledgeProof(zkp);

        // 测试范围证明（Range Proof）
        testRangeProof(zkp);

//        // 测试值相等性证明（Value Equality）
        testValueEqualityProof(zkp);
//
//        // 测试多重和关系证明（Multi-Sum Relationship）
        testMultiSumRelationship(zkp);

        // 测试计算
        testCompute(zkp);
    }

    private static void testCompute(NativeInterface zkp) throws WedprException {
        System.out.println("\n=== Testing compute viewkey ===");
        byte[] blinding = generateRandomBytes(32);
        byte[] viewkey = zkp.computeViewkey(blinding).expectNoError().viewkey;
        System.out.println("viewkey:" + HexFormat.of().formatHex(viewkey));

    }

    private static void testKnowledgeProof(NativeInterface zkp) throws WedprException {
        System.out.println("\n=== Testing Knowledge Proof ===");

        int value = 100;
        byte[] blinding = generateRandomBytes(32);

        // 生成知识证明（假设返回的字节数组包含证明）
        byte[] proof = zkp.proveKnowledgeProof(value, blinding).expectNoError().proof;
        if (proof == null) {
            System.out.println("Prove Knowledge Proof failed!");
            return;
        }
//        System.out.println("proof:" + HexFormat.of().formatHex(proof));

        // 生成承诺（假设由其他方法生成，这里仅为示例）
        // 注意：实际应根据库的实现获取正确的commitment
        byte[] commitment = zkp.computeCommitment(value, blinding).expectNoError().commitment;
        byte[] commitmentError = zkp.computeCommitment(value+1, blinding).expectNoError().commitment;

//        // 验证知识证明
        boolean isValid = zkp.verifyKnowledgeProof(commitment, proof).expectNoError().result;
        System.out.println("Knowledge Proof Verification: " + isValid);
        boolean isFailed = zkp.verifyKnowledgeProof(commitmentError, proof).expectNoError().result;
        System.out.println("Knowledge error Proof Verification: " + isFailed);
    }

    private static void testRangeProof(NativeInterface zkp) throws WedprException {
        System.out.println("\n=== Testing Range Proof ===");

        int value = 50;
        byte[] blinding = generateRandomBytes(32);

        // 生成范围证明
        byte[] proof = zkp.proveRangeProof(value, blinding).expectNoError().proof;
        if (proof == null) {
            System.out.println("Prove Range Proof failed!");
            return;
        }

        // 生成承诺
        byte[] commitment = zkp.computeCommitment(value, blinding).expectNoError().commitment;
        byte[] commitmentError = zkp.computeCommitment(value+1, blinding).expectNoError().commitment;

        // 验证范围证明
        boolean isValid = zkp.verifyRangeProof(commitment, proof).expectNoError().result;
        System.out.println("Range Proof Verification: " + isValid);
        boolean isFailed = zkp.verifyRangeProof(commitmentError, proof).expectNoError().result;
        System.out.println("Range error Proof Verification: " + isFailed);
    }

    private static void testValueEqualityProof(NativeInterface zkp) throws WedprException {
        System.out.println("\n=== Testing Value Equality Proof ===");

        int value1 = 200;
        int value2 = 150;
//        int value3 = 50;
        byte[] blinding1 = generateRandomBytes(32);
        byte[] blinding2 = generateRandomBytes(32);
//        byte[] blinding3 = generateRandomBytes(32);

        // 生成两个不同盲因子的相同值承诺（示例）
        byte[] commitment1 = zkp.computeCommitment(value1, blinding1).expectNoError().commitment;
        byte[] commitment2 = zkp.computeCommitment(value2, blinding2).expectNoError().commitment;
//        byte[] commitment3 = zkp.computeCommitment(value3, blinding3).expectNoError().Commitment;

        // 生成值相等性证明（假设证明两个承诺的值相同）
        byte[] proof = zkp.proveValueEqualityRelationshipProof(value1, blinding1).expectNoError().proof;
        if (proof == null) {
            System.out.println("Prove Value Equality failed!");
            return;
        }

        // 验证证明（示例参数，实际可能需要两个commitment）
        boolean isValid = zkp.verifyValueEqualityRelationshipProof(value1, commitment1, proof).expectNoError().result;
        System.out.println("Value Equality Verification: " + isValid);

        boolean isFailed = zkp.verifyValueEqualityRelationshipProof(value2, commitment1, proof).expectNoError().result;
        System.out.println("Value Equality error Verification: " + isFailed);
    }

    private static void testMultiSumRelationship(NativeInterface zkp) throws WedprException {
        System.out.println("\n=== Testing Multi-Sum Relationship Proof ===");

        // 示例参数
        int[] senderValues = {10, 20};
        int[] receiverValues = {30};
        byte[][] senderBlindings = {generateRandomBytes(32), generateRandomBytes(32)};
        byte[][] receiverBlindings = {generateRandomBytes(32)};

        List<byte[]> inputCommitments = new ArrayList<>();
        for (int i = 0; i < senderValues.length; i++) {
            inputCommitments.add(zkp.computeCommitment(senderValues[i], senderBlindings[i]).expectNoError().commitment);
        }

        for (int i = 0; i < inputCommitments.size(); i++) {
            System.out.println("inputCommitments: " + HexFormat.of().formatHex(inputCommitments.get(i)));
        }

        byte[] outputCommitment = zkp.computeCommitment(receiverValues[0], receiverBlindings[0]).expectNoError().commitment;
        byte[] errorOutputCommitment = zkp.computeCommitment(receiverValues[0]+1, receiverBlindings[0]).expectNoError().commitment;
        System.out.println("outputCommitment: " + HexFormat.of().formatHex(outputCommitment));


        // 发送方Setup
        List<byte[]> senderSetupPublicPartList = new ArrayList<>();
        List<byte[]> senderSetupPrivatePartList = new ArrayList<>();
        for (int i = 0; i < senderBlindings.length; i++) {
            ZkpResult senderSetupResult = zkp.senderProveMultiSumRelationshipSetup(
                    senderValues[i], senderBlindings[i]).expectNoError();
            senderSetupPublicPartList.add(senderSetupResult.publicPart);
            senderSetupPrivatePartList.add(senderSetupResult.privatePart);
        }


        // 接收方Setup
        ZkpResult receiverSetup = zkp.receiverProveMultiSumRelationshipSetup(
                receiverValues[0], receiverBlindings[0]).expectNoError();


        // 协调者Setup
        byte[] check = zkp.coordinatorProveMultiSumRelationshipSetup(
                concatBytesArray(senderSetupPublicPartList), receiverSetup.publicPart).expectNoError().check;

        // 发送方Final（示例参数）
        List<byte[]> senderSetupFinalPartList = new ArrayList<>();

        for (int i = 0; i < senderBlindings.length; i++) {
            ZkpResult senderSetupResult = zkp.senderProveMultiSumRelationshipFinal(
                    senderValues[i], senderBlindings[i], senderSetupPrivatePartList.get(i), check).expectNoError();
            senderSetupFinalPartList.add(senderSetupResult.publicPart);
        }

        // 接收方Final
        byte[] receiverFinal = zkp.receiverProveMultiSumRelationshipFinal(
                receiverBlindings[0], receiverSetup.privatePart, check).expectNoError().publicPart;

        // 协调者Final
        byte[] proof = zkp.coordinatorProveMultiSumRelationshipFinal(
                check, concatBytesArray(senderSetupFinalPartList), receiverFinal).expectNoError().proof;

        // 验证多重和证明（示例参数）
        System.out.println("concatBytesArray(inputCommitments): " + HexFormat.of().formatHex(concatBytesArray(inputCommitments)));

        boolean isValid = zkp.verifyMultiSumRelationship(concatBytesArray(inputCommitments), outputCommitment, proof).expectNoError().result;
        boolean isFailed = zkp.verifyMultiSumRelationship(concatBytesArray(inputCommitments), errorOutputCommitment, proof).expectNoError().result;
        System.out.println("Multi-Sum Verification: " + isValid);
        System.out.println("Multi-Sum error Verification: " + isFailed);
    }

    // 生成随机字节数组
    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    // 示例生成承诺的方法（实际应由库提供）
    private static byte[] generateDummyCommitment(int value, byte[] blinding) {
        // 注意：此处仅为示例，实际应调用库中的承诺生成方法
        return (value + "-" + new String(blinding)).getBytes();
    }

    // 辅助方法：数组合和
    private static int sumArray(int[] arr) {
        int sum = 0;
        for (int num : arr) sum += num;
        return sum;
    }

    // 辅助方法：拼接字节数组
    private static byte[] concatBytesArray(byte[][] arrays) {
        // 示例实现，实际可能需要更严谨的拼接方式
        int totalLength = 0;
        for (byte[] array : arrays) totalLength += array.length;
        byte[] result = new byte[totalLength];
        int destPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, destPos, array.length);
            destPos += array.length;
        }
        return result;
    }

    private static byte[] concatBytesArray(List<byte[]> arrays) {
        // 示例实现，实际可能需要更严谨的拼接方式
        int totalLength = 0;
        for (byte[] array : arrays) totalLength += array.length;
        byte[] result = new byte[totalLength];
        int destPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, destPos, array.length);
            destPos += array.length;
        }
        return result;
    }
}