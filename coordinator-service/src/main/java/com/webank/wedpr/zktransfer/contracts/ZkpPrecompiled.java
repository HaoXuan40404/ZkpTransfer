package com.webank.wedpr.zktransfer.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class ZkpPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"point1\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"point2\",\"type\":\"bytes\"}],\"name\":\"aggregatePoint\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"},{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"selector\":[1740121632,2819513926],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c1_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c2_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c3_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c_basepoint\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"blinding_basepoint\",\"type\":\"bytes\"}],\"name\":\"verifyEitherEqualityProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1041658903,3347098223],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c1_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c2_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"basepoint1\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"basepoint2\",\"type\":\"bytes\"}],\"name\":\"verifyEqualityProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1002371866,3599365476],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c1_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c2_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c1_basepoint\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c2_basepoint\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"blinding_basepoint\",\"type\":\"bytes\"}],\"name\":\"verifyFormatProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3585873698,2009281023],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c_basepoint\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"blinding_basepoint\",\"type\":\"bytes\"}],\"name\":\"verifyKnowledgeProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3689247579,759749773],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"}],\"name\":\"verifyKnowledgeProofWithoutBasePoint\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1920626837,3585937449],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"input_points\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"output_points\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"}],\"name\":\"verifyMultiSumProofWithoutBasePoint\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[421495251,3316823951],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c1_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c2_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c3_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"value_basepoint\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"blinding_basepoint\",\"type\":\"bytes\"}],\"name\":\"verifyProductProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1909901088,2288598737],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"blinding_basepoint\",\"type\":\"bytes\"}],\"name\":\"verifyRangeProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1225272167,3431285278],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"}],\"name\":\"verifyRangeProofWithoutBasePoint\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1866083868,4091456109],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c1_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c2_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c3_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"value_basepoint\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"blinding_basepoint\",\"type\":\"bytes\"}],\"name\":\"verifySumProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1091212888,889703933],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"c1_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c2_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"c3_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"}],\"name\":\"verifySumProofWithoutBasePoint\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[4042948784,2800274116],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint64\",\"name\":\"c_value\",\"type\":\"uint64\"},{\"internalType\":\"bytes\",\"name\":\"c_point\",\"type\":\"bytes\"},{\"internalType\":\"bytes\",\"name\":\"proof\",\"type\":\"bytes\"}],\"name\":\"verifyValueEqualityProofWithoutBasePoint\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3371791266,869834117],\"stateMutability\":\"view\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AGGREGATEPOINT = "aggregatePoint";

    public static final String FUNC_VERIFYEITHEREQUALITYPROOF = "verifyEitherEqualityProof";

    public static final String FUNC_VERIFYEQUALITYPROOF = "verifyEqualityProof";

    public static final String FUNC_VERIFYFORMATPROOF = "verifyFormatProof";

    public static final String FUNC_VERIFYKNOWLEDGEPROOF = "verifyKnowledgeProof";

    public static final String FUNC_VERIFYKNOWLEDGEPROOFWITHOUTBASEPOINT = "verifyKnowledgeProofWithoutBasePoint";

    public static final String FUNC_VERIFYMULTISUMPROOFWITHOUTBASEPOINT = "verifyMultiSumProofWithoutBasePoint";

    public static final String FUNC_VERIFYPRODUCTPROOF = "verifyProductProof";

    public static final String FUNC_VERIFYRANGEPROOF = "verifyRangeProof";

    public static final String FUNC_VERIFYRANGEPROOFWITHOUTBASEPOINT = "verifyRangeProofWithoutBasePoint";

    public static final String FUNC_VERIFYSUMPROOF = "verifySumProof";

    public static final String FUNC_VERIFYSUMPROOFWITHOUTBASEPOINT = "verifySumProofWithoutBasePoint";

    public static final String FUNC_VERIFYVALUEEQUALITYPROOFWITHOUTBASEPOINT = "verifyValueEqualityProofWithoutBasePoint";

    protected ZkpPrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Tuple2<Boolean, byte[]> aggregatePoint(byte[] point1, byte[] point2) throws
            ContractException {
        final Function function = new Function(FUNC_AGGREGATEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(point1), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(point2)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<DynamicBytes>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<Boolean, byte[]>(
                (Boolean) results.get(0).getValue(), 
                (byte[]) results.get(1).getValue());
    }

    public Function getMethodAggregatePointRawFunction(byte[] point1, byte[] point2) throws
            ContractException {
        final Function function = new Function(FUNC_AGGREGATEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(point1), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(point2)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<DynamicBytes>() {}));
        return function;
    }

    public Boolean verifyEitherEqualityProof(byte[] c1_point, byte[] c2_point, byte[] c3_point,
            byte[] proof, byte[] c_basepoint, byte[] blinding_basepoint) throws ContractException {
        final Function function = new Function(FUNC_VERIFYEITHEREQUALITYPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyEitherEqualityProofRawFunction(byte[] c1_point, byte[] c2_point,
            byte[] c3_point, byte[] proof, byte[] c_basepoint, byte[] blinding_basepoint) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYEITHEREQUALITYPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyEqualityProof(byte[] c1_point, byte[] c2_point, byte[] proof,
            byte[] basepoint1, byte[] basepoint2) throws ContractException {
        final Function function = new Function(FUNC_VERIFYEQUALITYPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(basepoint1), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(basepoint2)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyEqualityProofRawFunction(byte[] c1_point, byte[] c2_point,
            byte[] proof, byte[] basepoint1, byte[] basepoint2) throws ContractException {
        final Function function = new Function(FUNC_VERIFYEQUALITYPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(basepoint1), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(basepoint2)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyFormatProof(byte[] c1_point, byte[] c2_point, byte[] proof,
            byte[] c1_basepoint, byte[] c2_basepoint, byte[] blinding_basepoint) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYFORMATPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyFormatProofRawFunction(byte[] c1_point, byte[] c2_point,
            byte[] proof, byte[] c1_basepoint, byte[] c2_basepoint, byte[] blinding_basepoint)
            throws ContractException {
        final Function function = new Function(FUNC_VERIFYFORMATPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyKnowledgeProof(byte[] c_point, byte[] proof, byte[] c_basepoint,
            byte[] blinding_basepoint) throws ContractException {
        final Function function = new Function(FUNC_VERIFYKNOWLEDGEPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyKnowledgeProofRawFunction(byte[] c_point, byte[] proof,
            byte[] c_basepoint, byte[] blinding_basepoint) throws ContractException {
        final Function function = new Function(FUNC_VERIFYKNOWLEDGEPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyKnowledgeProofWithoutBasePoint(byte[] c_point, byte[] proof) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYKNOWLEDGEPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyKnowledgeProofWithoutBasePointRawFunction(byte[] c_point,
            byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYKNOWLEDGEPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyMultiSumProofWithoutBasePoint(byte[] input_points, byte[] output_points,
            byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYMULTISUMPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(input_points), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(output_points), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyMultiSumProofWithoutBasePointRawFunction(byte[] input_points,
            byte[] output_points, byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYMULTISUMPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(input_points), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(output_points), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyProductProof(byte[] c1_point, byte[] c2_point, byte[] c3_point,
            byte[] proof, byte[] value_basepoint, byte[] blinding_basepoint) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYPRODUCTPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(value_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyProductProofRawFunction(byte[] c1_point, byte[] c2_point,
            byte[] c3_point, byte[] proof, byte[] value_basepoint, byte[] blinding_basepoint) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYPRODUCTPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(value_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyRangeProof(byte[] c_point, byte[] proof, byte[] blinding_basepoint) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYRANGEPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyRangeProofRawFunction(byte[] c_point, byte[] proof,
            byte[] blinding_basepoint) throws ContractException {
        final Function function = new Function(FUNC_VERIFYRANGEPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyRangeProofWithoutBasePoint(byte[] c_point, byte[] proof) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYRANGEPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyRangeProofWithoutBasePointRawFunction(byte[] c_point,
            byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYRANGEPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifySumProof(byte[] c1_point, byte[] c2_point, byte[] c3_point, byte[] proof,
            byte[] value_basepoint, byte[] blinding_basepoint) throws ContractException {
        final Function function = new Function(FUNC_VERIFYSUMPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(value_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifySumProofRawFunction(byte[] c1_point, byte[] c2_point,
            byte[] c3_point, byte[] proof, byte[] value_basepoint, byte[] blinding_basepoint) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYSUMPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(value_basepoint), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(blinding_basepoint)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifySumProofWithoutBasePoint(byte[] c1_point, byte[] c2_point, byte[] c3_point,
            byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYSUMPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifySumProofWithoutBasePointRawFunction(byte[] c1_point,
            byte[] c2_point, byte[] c3_point, byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYSUMPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c1_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c2_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c3_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifyValueEqualityProofWithoutBasePoint(BigInteger c_value, byte[] c_point,
            byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYVALUEEQUALITYPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint64(c_value), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyValueEqualityProofWithoutBasePointRawFunction(BigInteger c_value,
            byte[] c_point, byte[] proof) throws ContractException {
        final Function function = new Function(FUNC_VERIFYVALUEEQUALITYPROOFWITHOUTBASEPOINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint64(c_value), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(c_point), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes(proof)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public static ZkpPrecompiled load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new ZkpPrecompiled(contractAddress, client, credential);
    }

    public static ZkpPrecompiled deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(ZkpPrecompiled.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }
}
