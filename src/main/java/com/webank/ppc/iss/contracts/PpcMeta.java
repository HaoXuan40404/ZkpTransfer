package com.webank.ppc.iss.contracts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class PpcMeta extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50610750806100206000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c8063bd87fe051161005b578063bd87fe05146100fd578063bdd9f4d514610110578063e868b55c1461014c578063f5cdf9c31461018857600080fd5b806331f9d5171461008d57806366cd55e7146100b55780636de12905146100c8578063a457f756146100dd575b600080fd5b6100a061009b3660046105bf565b61019b565b60405190151581526020015b60405180910390f35b6100a06100c3366004610623565b610215565b6100db6100d63660046105bf565b61023f565b005b6100f06100eb366004610623565b61030f565b6040516100ac9190610690565b6100db61010b3660046105bf565b6103c2565b6100f06040518060400160405280601c81526020017f746865206461746120696e666f20616c7265616479206578697374730000000081525081565b6100f06040518060400160405280601c81526020017f746865206461746120696e666f206973206e6f7420657869737465640000000081525081565b6100db610196366004610623565b61041e565b6000806101a784610215565b604080518082018252600181526020810186905290519192509081906000906101d19088906106c3565b908152604051602091819003820190208251815460ff1916901515178155828201518051919261020992600185019290910190610483565b50929695505050505050565b6000808260405161022691906106c3565b9081526040519081900360200190205460ff1692915050565b61024882610215565b60408051808201909152601c81527f746865206461746120696e666f20616c7265616479206578697374730000000060208201529015156001146102a85760405162461bcd60e51b815260040161029f9190610690565b60405180910390fd5b506040805180820182526001815260208101839052905181906000906102cf9086906106c3565b908152604051602091819003820190208251815460ff1916901515178155828201518051919261030792600185019290910190610483565b505050505050565b606060008260405161032191906106c3565b9081526020016040518091039020600101805461033d906106df565b80601f0160208091040260200160405190810160405280929190818152602001828054610369906106df565b80156103b65780601f1061038b576101008083540402835291602001916103b6565b820191906000526020600020905b81548152906001019060200180831161039957829003601f168201915b50505050509050919050565b6103cb82610215565b60408051808201909152601c81527f746865206461746120696e666f20616c72656164792065786973747300000000602082015290156102a85760405162461bcd60e51b815260040161029f9190610690565b6040805180820182526000808252606060208301529151909182916104449085906106c3565b908152604051602091819003820190208251815460ff1916901515178155828201518051919261047c92600185019290910190610483565b5050505050565b82805461048f906106df565b90600052602060002090601f0160209004810192826104b157600085556104f7565b82601f106104ca57805160ff19168380011785556104f7565b828001600101855582156104f7579182015b828111156104f75782518255916020019190600101906104dc565b50610503929150610507565b5090565b5b808211156105035760008155600101610508565b634e487b7160e01b600052604160045260246000fd5b600082601f83011261054357600080fd5b813567ffffffffffffffff8082111561055e5761055e61051c565b604051601f8301601f19908116603f011681019082821181831017156105865761058661051c565b8160405283815286602085880101111561059f57600080fd5b836020870160208301376000602085830101528094505050505092915050565b600080604083850312156105d257600080fd5b823567ffffffffffffffff808211156105ea57600080fd5b6105f686838701610532565b9350602085013591508082111561060c57600080fd5b5061061985828601610532565b9150509250929050565b60006020828403121561063557600080fd5b813567ffffffffffffffff81111561064c57600080fd5b61065884828501610532565b949350505050565b60005b8381101561067b578181015183820152602001610663565b8381111561068a576000848401525b50505050565b60208152600082518060208401526106af816040850160208701610660565b601f01601f19169190910160400192915050565b600082516106d5818460208701610660565b9190910192915050565b600181811c908216806106f357607f821691505b6020821081141561071457634e487b7160e01b600052602260045260246000fd5b5091905056fea26469706673582212207c3be2a82a0ef32db5c69a1a2f65d12301f9f331dd4cd54958a9cfdc617f430364736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50610756806100206000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c8063deebe7541161005b578063deebe7541461012a578063e20840e61461013d578063f798e33814610150578063fd739ab61461018c57600080fd5b806301b375391461008d578063270d7590146100df5780639772df13146100f4578063bd782e9e14610107575b600080fd5b6100c96040518060400160405280601c81526020017f746865206461746120696e666f206973206e6f7420657869737465640000000081525081565b6040516100d69190610552565b60405180910390f35b6100f26100ed366004610628565b61019f565b005b6100f2610102366004610665565b610204565b61011a610115366004610628565b6102d5565b60405190151581526020016100d6565b6100c9610138366004610628565b6102ff565b61011a61014b366004610665565b6103b2565b6100c96040518060400160405280601c81526020017f746865206461746120696e666f20616c7265616479206578697374730000000081525081565b6100f261019a366004610665565b61042c565b6040805180820182526000808252606060208301529151909182916101c59085906106c9565b908152604051602091819003820190208251815460ff191690151517815582820151805191926101fd92600185019290910190610489565b5050505050565b61020d826102d5565b60408051808201909152601c81527f746865206461746120696e666f20616c72656164792065786973747300000000602082015290151560011461026e57604051636381e58960e11b81526004016102659190610552565b60405180910390fd5b506040805180820182526001815260208101839052905181906000906102959086906106c9565b908152604051602091819003820190208251815460ff191690151517815582820151805191926102cd92600185019290910190610489565b505050505050565b600080826040516102e691906106c9565b9081526040519081900360200190205460ff1692915050565b606060008260405161031191906106c9565b9081526020016040518091039020600101805461032d906106e5565b80601f0160208091040260200160405190810160405280929190818152602001828054610359906106e5565b80156103a65780601f1061037b576101008083540402835291602001916103a6565b820191906000526020600020905b81548152906001019060200180831161038957829003601f168201915b50505050509050919050565b6000806103be846102d5565b604080518082018252600181526020810186905290519192509081906000906103e89088906106c9565b908152604051602091819003820190208251815460ff1916901515178155828201518051919261042092600185019290910190610489565b50929695505050505050565b610435826102d5565b60408051808201909152601c81527f746865206461746120696e666f20616c726561647920657869737473000000006020820152901561026e57604051636381e58960e11b81526004016102659190610552565b828054610495906106e5565b90600052602060002090601f0160209004810192826104b757600085556104fd565b82601f106104d057805160ff19168380011785556104fd565b828001600101855582156104fd579182015b828111156104fd5782518255916020019190600101906104e2565b5061050992915061050d565b5090565b5b80821115610509576000815560010161050e565b60005b8381101561053d578181015183820152602001610525565b8381111561054c576000848401525b50505050565b6020815260008251806020840152610571816040850160208701610522565b601f01601f19169190910160400192915050565b63b95aa35560e01b600052604160045260246000fd5b600082601f8301126105ac57600080fd5b813567ffffffffffffffff808211156105c7576105c7610585565b604051601f8301601f19908116603f011681019082821181831017156105ef576105ef610585565b8160405283815286602085880101111561060857600080fd5b836020870160208301376000602085830101528094505050505092915050565b60006020828403121561063a57600080fd5b813567ffffffffffffffff81111561065157600080fd5b61065d8482850161059b565b949350505050565b6000806040838503121561067857600080fd5b823567ffffffffffffffff8082111561069057600080fd5b61069c8683870161059b565b935060208501359150808211156106b257600080fd5b506106bf8582860161059b565b9150509250929050565b600082516106db818460208701610522565b9190910192915050565b600181811c908216806106f957607f821691505b6020821081141561071a5763b95aa35560e01b600052602260045260246000fd5b5091905056fea26469706673582212204df3089a71a8a0d1426a003b9ef6900f6b818df568663b22d1fa618eeb089d5764736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"conflictFields\":[{\"kind\":5}],\"inputs\":[],\"name\":\"ERROR_DATA_EXIST\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[3185177813,4153991992],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[],\"name\":\"ERROR_DATA_NOT_EXIST\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[3899176284,28538169],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"checkValueExists\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1724732903,3178770078],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"metaKey\",\"type\":\"string\"}],\"name\":\"queryMeta\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[2757228374,3740002132],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"metaKey\",\"type\":\"string\"}],\"name\":\"removeMeta\",\"outputs\":[],\"selector\":[4123916739,655193488],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"metaKey\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"metaValue\",\"type\":\"string\"}],\"name\":\"updateMeta\",\"outputs\":[],\"selector\":[1843472645,2540887827],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"metaKey\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"metaValue\",\"type\":\"string\"}],\"name\":\"uploadMeta\",\"outputs\":[],\"selector\":[3179806213,4252211894],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"metaKey\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"metaValue\",\"type\":\"string\"}],\"name\":\"uploadMetaWithoutCheck\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[838456599,3792191718],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_ERROR_DATA_EXIST = "ERROR_DATA_EXIST";

    public static final String FUNC_ERROR_DATA_NOT_EXIST = "ERROR_DATA_NOT_EXIST";

    public static final String FUNC_CHECKVALUEEXISTS = "checkValueExists";

    public static final String FUNC_QUERYMETA = "queryMeta";

    public static final String FUNC_REMOVEMETA = "removeMeta";

    public static final String FUNC_UPDATEMETA = "updateMeta";

    public static final String FUNC_UPLOADMETA = "uploadMeta";

    public static final String FUNC_UPLOADMETAWITHOUTCHECK = "uploadMetaWithoutCheck";

    protected PpcMeta(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public String ERROR_DATA_EXIST() throws ContractException {
        final Function function = new Function(FUNC_ERROR_DATA_EXIST,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String ERROR_DATA_NOT_EXIST() throws ContractException {
        final Function function = new Function(FUNC_ERROR_DATA_NOT_EXIST,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Boolean checkValueExists(String key) throws ContractException {
        final Function function = new Function(FUNC_CHECKVALUEEXISTS,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(key)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public String queryMeta(String metaKey) throws ContractException {
        final Function function = new Function(FUNC_QUERYMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt removeMeta(String metaKey) {
        final Function function = new Function(
                FUNC_REMOVEMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey)),
                Collections.<TypeReference<?>>emptyList(), 4);
        return executeTransaction(function);
    }

    public String getSignedTransactionForRemoveMeta(String metaKey) {
        final Function function = new Function(
                FUNC_REMOVEMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey)),
                Collections.<TypeReference<?>>emptyList(), 4);
        return createSignedTransaction(function);
    }

    public String removeMeta(String metaKey, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_REMOVEMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey)),
                Collections.<TypeReference<?>>emptyList(), 4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getRemoveMetaInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_REMOVEMETA,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>(

                (String) results.get(0).getValue()
                );
    }

    public TransactionReceipt updateMeta(String metaKey, String metaValue) {
        final Function function = new Function(
                FUNC_UPDATEMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForUpdateMeta(String metaKey, String metaValue) {
        final Function function = new Function(
                FUNC_UPDATEMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String updateMeta(String metaKey, String metaValue, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPDATEMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, String> getUpdateMetaInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPDATEMETA,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(

                (String) results.get(0).getValue(),
                (String) results.get(1).getValue()
                );
    }

    public TransactionReceipt uploadMeta(String metaKey, String metaValue) {
        final Function function = new Function(
                FUNC_UPLOADMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForUploadMeta(String metaKey, String metaValue) {
        final Function function = new Function(
                FUNC_UPLOADMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String uploadMeta(String metaKey, String metaValue, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPLOADMETA,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, String> getUploadMetaInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPLOADMETA,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(

                (String) results.get(0).getValue(),
                (String) results.get(1).getValue()
                );
    }

    public TransactionReceipt uploadMetaWithoutCheck(String metaKey, String metaValue) {
        final Function function = new Function(
                FUNC_UPLOADMETAWITHOUTCHECK,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForUploadMetaWithoutCheck(String metaKey, String metaValue) {
        final Function function = new Function(
                FUNC_UPLOADMETAWITHOUTCHECK,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String uploadMetaWithoutCheck(String metaKey, String metaValue,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPLOADMETAWITHOUTCHECK,
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaKey),
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(metaValue)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, String> getUploadMetaWithoutCheckInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPLOADMETAWITHOUTCHECK,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(

                (String) results.get(0).getValue(),
                (String) results.get(1).getValue()
                );
    }

    public Tuple1<Boolean> getUploadMetaWithoutCheckOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_UPLOADMETAWITHOUTCHECK,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>(

                (Boolean) results.get(0).getValue()
                );
    }

    public static PpcMeta load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new PpcMeta(contractAddress, client, credential);
    }

    public static PpcMeta deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(PpcMeta.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }
}
