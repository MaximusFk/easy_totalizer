package com.easytotalizer.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.1.1.
 */
public final class EasyTotalizer extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60048054600160a060020a03191633600160a060020a0316179055610a70806100396000396000f3006060604052600436106100f05763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630aebeb4e81146100f25780633d246cc41461010857806341c0e1b51461012d5780634a79d50c146101405780635def1113146101ca57806361203265146101fb57806370ba11131461022a57806370eccd06146102535780637284e416146102665780637365870b1461027957806375cd4c6f1461028457806376cdb03b146102c5578063a9eaf933146102d8578063c2b6b58c146102eb578063c38a8afd14610312578063c870554414610325578063dfbf53ae14610338575b005b34156100fd57600080fd5b6100f060043561034b565b341561011357600080fd5b61011b610460565b60405190815260200160405180910390f35b341561013857600080fd5b6100f0610467565b341561014b57600080fd5b61015361048e565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561018f578082015183820152602001610177565b50505050905090810190601f1680156101bc5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101d557600080fd5b6101e060043561052c565b60405191825260ff1660208201526040908101905180910390f35b341561020657600080fd5b61020e61055b565b604051600160a060020a03909116815260200160405180910390f35b341561023557600080fd5b61023d61056a565b60405160ff909116815260200160405180910390f35b341561025e57600080fd5b61011b61058b565b341561027157600080fd5b61015361064d565b6100f06004356106b8565b341561028f57600080fd5b6100f060246004803582810192908201359181358083019290820135916044359160ff60643516916084359182019101356107e9565b34156102d057600080fd5b61011b6108fe565b34156102e357600080fd5b61011b610904565b34156102f657600080fd5b6102fe610909565b604051901515815260200160405180910390f35b341561031d57600080fd5b61011b610912565b341561033057600080fd5b61011b610918565b341561034357600080fd5b6101e061091e565b60045460009033600160a060020a0390811691161461036957600080fd5b6005548290811061037957600080fd5b60095460ff161561038957600080fd5b6009805460ff1916600117905560058054849081106103a457fe5b600091825260209091206002909102018054600655600101546007805460ff191660ff9092169190911790556103d861092a565b60075460035491935060ff16908390038115156103f157fe5b04600a55600454600160a060020a031682156108fc0283604051600060405180830381858888f19350505050151561042857600080fd5b7f6cc09e7b5c3e49861ebe8f6867e1618fbfc14c8d0e968fde37c4243ca02a6f838360405190815260200160405180910390a1505050565b6005545b90565b60045433600160a060020a0390811691161461048257600080fd5b33600160a060020a0316ff5b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105245780601f106104f957610100808354040283529160200191610524565b820191906000526020600020905b81548152906001019060200180831161050757829003601f168201915b505050505081565b600580548290811061053a57fe5b60009182526020909120600290910201805460019091015490915060ff1682565b600454600160a060020a031681565b60045474010000000000000000000000000000000000000000900460ff1681565b60095460009060ff16151561059f57600080fd5b33600160a060020a0316600090815260086020526040902054610100900460ff16156105ca57600080fd5b6000600a54111561064657600160a060020a03331660008181526008602052604090819020805461ff001916610100179055600a5480156108fc029151600060405180830381858888f1935050505015156106465750600160a060020a0333166000908152600860205260408120805461ff0019169055610464565b50600a5490565b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105245780601f106104f957610100808354040283529160200191610524565b600554819081106106c857600080fd5b60095460ff16156106d857600080fd5b6002543410156106e757600080fd5b600160a060020a03331660009081526008602052604090205460ff161561070d57600080fd5b606060405190810160409081526001825260006020808401829052828401869052600160a060020a033316825260089052208151815460ff1916901515178155602082015181549015156101000261ff00199091161781556040820151600190910155506003805434019055600580548390811061078757fe5b600091825260209091206002909102016001908101805460ff19811660ff918216909301169190911790557f0f87c9925438028381522cc90a971b146ae7ed65239b187b1771cf8341bf38fb8260405190815260200160405180910390a15050565b60045460009033600160a060020a0390811691161461080757600080fd5b61081360008a8a610955565b5061082060018888610955565b505060028490556004805474ff000000000000000000000000000000000000000019167401000000000000000000000000000000000000000060ff86160217905560005b81811080156108735750602081105b156108f357600580546001810161088a83826109d3565b9160005260206000209060020201600060408051908101604052808787878181106108b157fe5b6020908102929092013583525060009101529190508151815560208201516001918201805460ff191660ff929092169190911790559290920191506108649050565b505050505050505050565b60035481565b602081565b60095460ff1681565b60025481565b600a5481565b60065460075460ff1682565b6004546003547401000000000000000000000000000000000000000090910460ff1660649091040290565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106109965782800160ff198235161785556109c3565b828001600101855582156109c3579182015b828111156109c35782358255916020019190600101906109a8565b506109cf929150610a04565b5090565b8154818355818115116109ff576002028160020283600052602060002091820191016109ff9190610a1e565b505050565b61046491905b808211156109cf5760008155600101610a0a565b61046491905b808211156109cf576000815560018101805460ff19169055600201610a245600a165627a7a72305820eb455497a1e535a0914e1983ab9534d78d962e045360da1e9dca3d6acf17c1cc0029";

    private EasyTotalizer(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private EasyTotalizer(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<ClosedEventResponse> getClosedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Closed", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ClosedEventResponse> responses = new ArrayList<ClosedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ClosedEventResponse typedResponse = new ClosedEventResponse();
            typedResponse._winner = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ClosedEventResponse> closedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Closed", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ClosedEventResponse>() {
            @Override
            public ClosedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ClosedEventResponse typedResponse = new ClosedEventResponse();
                typedResponse._winner = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<BetMadeEventResponse> getBetMadeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("BetMade", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<BetMadeEventResponse> responses = new ArrayList<BetMadeEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            BetMadeEventResponse typedResponse = new BetMadeEventResponse();
            typedResponse._variant = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BetMadeEventResponse> betMadeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("BetMade", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, BetMadeEventResponse>() {
            @Override
            public BetMadeEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                BetMadeEventResponse typedResponse = new BetMadeEventResponse();
                typedResponse._variant = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> close(BigInteger _winner) {
        Function function = new Function(
                "close", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_winner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getVariantsCount() {
        Function function = new Function("getVariantsCount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> kill() {
        Function function = new Function(
                "kill", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> title() {
        Function function = new Function("title", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Tuple2<byte[], BigInteger>> variants(BigInteger param0) {
        final Function function = new Function("variants", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple2<byte[], BigInteger>>(
                new Callable<Tuple2<byte[], BigInteger>>() {
                    @Override
                    public Tuple2<byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<String> organizer() {
        Function function = new Function("organizer", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> percent() {
        Function function = new Function("percent", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> takeReward() {
        Function function = new Function(
                "takeReward", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> description() {
        Function function = new Function("description", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> bet(BigInteger _variant, BigInteger weiValue) {
        Function function = new Function(
                "bet", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_variant)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> init(String _title, String _desc, BigInteger _minBet, BigInteger _percent, List<byte[]> _variants) {
        Function function = new Function(
                "init", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_title), 
                new org.web3j.abi.datatypes.Utf8String(_desc), 
                new org.web3j.abi.datatypes.generated.Uint256(_minBet), 
                new org.web3j.abi.datatypes.generated.Uint8(_percent), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(_variants, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> bank() {
        Function function = new Function("bank", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> MAX_VARIANTS() {
        Function function = new Function("MAX_VARIANTS", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> isClosed() {
        Function function = new Function("isClosed", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> minimumBet() {
        Function function = new Function("minimumBet", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> payAmount() {
        Function function = new Function("payAmount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple2<byte[], BigInteger>> winner() {
        final Function function = new Function("winner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple2<byte[], BigInteger>>(
                new Callable<Tuple2<byte[], BigInteger>>() {
                    @Override
                    public Tuple2<byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public static RemoteCall<EasyTotalizer> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EasyTotalizer.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<EasyTotalizer> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EasyTotalizer.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static EasyTotalizer load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EasyTotalizer(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static EasyTotalizer load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EasyTotalizer(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class ClosedEventResponse {
        public BigInteger _winner;
    }

    public static class BetMadeEventResponse {
        public BigInteger _variant;
    }
}
