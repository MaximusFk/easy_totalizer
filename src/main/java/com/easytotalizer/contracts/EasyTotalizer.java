package com.easytotalizer.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.0.1.
 */
public final class EasyTotalizer extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b6040516109ae3803806109ae833981016040528080518201919060200180518201919060200180519190602001805191906020018051919060200180519091019050600080878051610065929160200190610156565b506001868051610079929160200190610156565b505060028490556004805460a060020a60ff0219167401000000000000000000000000000000000000000060ff86160217600160a060020a031916600160a060020a03841617905560005b815181101561014a5760058054600181016100df83826101d4565b91600052602060002090600202016000604080519081016040528086868151811061010657fe5b90602001906020020151815260006020909101529190508151815560208201516001918201805460ff191660ff929092169190911790559290920191506100c49050565b50505050505050610248565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061019757805160ff19168380011785556101c4565b828001600101855582156101c4579182015b828111156101c45782518255916020019190600101906101a9565b506101d0929150610205565b5090565b815481835581811511610200576002028160020283600052602060002091820191016102009190610222565b505050565b61021f91905b808211156101d0576000815560010161020b565b90565b61021f91905b808211156101d0576000815560018101805460ff19169055600201610228565b610757806102576000396000f3006060604052600436106100cf5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630aebeb4e81146100d45780633d246cc4146100ec5780634a79d50c146101115780635def11131461019b57806361203265146101cc57806370ba1113146101fb57806370eccd06146102245780637284e416146102375780637365870b1461024a57806376cdb03b14610255578063c2b6b58c14610268578063c38a8afd1461028f578063c8705544146102a2578063dfbf53ae146102b5575b600080fd5b34156100df57600080fd5b6100ea6004356102c8565b005b34156100f757600080fd5b6100ff6103aa565b60405190815260200160405180910390f35b341561011c57600080fd5b6101246103b1565b60405160208082528190810183818151815260200191508051906020019080838360005b83811015610160578082015183820152602001610148565b50505050905090810190601f16801561018d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101a657600080fd5b6101b160043561044f565b60405191825260ff1660208201526040908101905180910390f35b34156101d757600080fd5b6101df61047e565b604051600160a060020a03909116815260200160405180910390f35b341561020657600080fd5b61020e61048d565b60405160ff909116815260200160405180910390f35b341561022f57600080fd5b6100ff6104ae565b341561024257600080fd5b610124610570565b6100ea6004356105db565b341561026057600080fd5b6100ff6106d9565b341561027357600080fd5b61027b6106df565b604051901515815260200160405180910390f35b341561029a57600080fd5b6100ff6106e8565b34156102ad57600080fd5b6100ff6106ee565b34156102c057600080fd5b6101b16106f4565b60045460009033600160a060020a039081169116146102e657600080fd5b600554829081106102f657600080fd5b60095460ff161561030657600080fd5b6009805460ff19166001179055600580548490811061032157fe5b600091825260209091206002909102018054600655600101546007805460ff191660ff909216919091179055610355610700565b60075460035491935060ff169083900381151561036e57fe5b04600a55600454600160a060020a031682156108fc0283604051600060405180830381858888f1935050505015156103a557600080fd5b505050565b6005545b90565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104475780601f1061041c57610100808354040283529160200191610447565b820191906000526020600020905b81548152906001019060200180831161042a57829003601f168201915b505050505081565b600580548290811061045d57fe5b60009182526020909120600290910201805460019091015490915060ff1682565b600454600160a060020a031681565b60045474010000000000000000000000000000000000000000900460ff1681565b60095460009060ff1615156104c257600080fd5b33600160a060020a0316600090815260086020526040902054610100900460ff16156104ed57600080fd5b6000600a54111561056957600160a060020a03331660008181526008602052604090819020805461ff001916610100179055600a5480156108fc029151600060405180830381858888f1935050505015156105695750600160a060020a0333166000908152600860205260408120805461ff00191690556103ae565b50600a5490565b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104475780601f1061041c57610100808354040283529160200191610447565b600554819081106105eb57600080fd5b60095460ff16156105fb57600080fd5b60025434101561060a57600080fd5b600160a060020a03331660009081526008602052604090205460ff161561063057600080fd5b606060405190810160409081526001825260006020808401829052828401869052600160a060020a033316825260089052208151815460ff1916901515178155602082015181549015156101000261ff0019909116178155604082015160019091015550600380543401905560058054839081106106aa57fe5b600091825260209091206002909102016001908101805460ff19811660ff918216909301169190911790555050565b60035481565b60095460ff1681565b60025481565b600a5481565b60065460075460ff1682565b6004546003547401000000000000000000000000000000000000000090910460ff16606490910402905600a165627a7a723058207d26ac156c25cfd4e230963de728fdac924bc052e9a3cd6ffb102b0850096e0e0029";

    private EasyTotalizer(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private EasyTotalizer(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> close(Uint256 _winner) {
        Function function = new Function(
                "close", 
                Arrays.<Type>asList(_winner), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Uint256> getVariantsCount() {
        Function function = new Function("getVariantsCount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Utf8String> title() {
        Function function = new Function("title", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Tuple2<Bytes32, Uint8>> variants(Uint256 param0) {
        final Function function = new Function("variants", 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple2<Bytes32, Uint8>>(
                new Callable<Tuple2<Bytes32, Uint8>>() {
                    @Override
                    public Tuple2<Bytes32, Uint8> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<Bytes32, Uint8>(
                                (Bytes32) results.get(0), 
                                (Uint8) results.get(1));
                    }
                });
    }

    public RemoteCall<Address> organizer() {
        Function function = new Function("organizer", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint8> percent() {
        Function function = new Function("percent", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> takeReward() {
        Function function = new Function(
                "takeReward", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Utf8String> description() {
        Function function = new Function("description", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> bet(Uint256 _variant, BigInteger weiValue) {
        Function function = new Function(
                "bet", 
                Arrays.<Type>asList(_variant), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<Uint256> bank() {
        Function function = new Function("bank", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Bool> isClosed() {
        Function function = new Function("isClosed", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> minimumBet() {
        Function function = new Function("minimumBet", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Uint256> payAmount() {
        Function function = new Function("payAmount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<Tuple2<Bytes32, Uint8>> winner() {
        final Function function = new Function("winner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple2<Bytes32, Uint8>>(
                new Callable<Tuple2<Bytes32, Uint8>>() {
                    @Override
                    public Tuple2<Bytes32, Uint8> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<Bytes32, Uint8>(
                                (Bytes32) results.get(0), 
                                (Uint8) results.get(1));
                    }
                });
    }

    public static RemoteCall<EasyTotalizer> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, Utf8String _title, Utf8String _desc, Uint256 _minBet, Uint8 _percent, Address _organizer, DynamicArray<Bytes32> _variants) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_title, _desc, _minBet, _percent, _organizer, _variants));
        return deployRemoteCall(EasyTotalizer.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<EasyTotalizer> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, Utf8String _title, Utf8String _desc, Uint256 _minBet, Uint8 _percent, Address _organizer, DynamicArray<Bytes32> _variants) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_title, _desc, _minBet, _percent, _organizer, _variants));
        return deployRemoteCall(EasyTotalizer.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static EasyTotalizer load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EasyTotalizer(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static EasyTotalizer load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EasyTotalizer(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
