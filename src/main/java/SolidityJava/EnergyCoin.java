package SolidityJava;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class EnergyCoin extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b506040516200190f3803806200190f833981810160405260a08110156200003757600080fd5b81019080805160405193929190846401000000008211156200005857600080fd5b838201915060208201858111156200006f57600080fd5b82518660018202830111640100000000821117156200008d57600080fd5b8083526020830192505050908051906020019080838360005b83811015620000c3578082015181840152602081019050620000a6565b50505050905090810190601f168015620000f15780820380516001836020036101000a031916815260200191505b50604052602001805160405193929190846401000000008211156200011557600080fd5b838201915060208201858111156200012c57600080fd5b82518660018202830111640100000000821117156200014a57600080fd5b8083526020830192505050908051906020019080838360005b838110156200018057808201518184015260208101905062000163565b50505050905090810190601f168015620001ae5780820380516001836020036101000a031916815260200191505b506040526020018051906020019092919080519060200190929190805190602001909291905050508360019080519060200190620001ee92919062000314565b5084600290805190602001906200020792919062000314565b5082600060046101000a81548163ffffffff021916908363ffffffff160217905550816000806101000a81548163ffffffff021916908363ffffffff16021790555081600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff16021790555033600960006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600960146101000a81548163ffffffff021916908363ffffffff1602179055505050505050620003c3565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200035757805160ff191683800117855562000388565b8280016001018555821562000388579182015b82811115620003875782518255916020019190600101906200036a565b5b5090506200039791906200039b565b5090565b620003c091905b80821115620003bc576000816000905550600101620003a2565b5090565b90565b61153c80620003d36000396000f3fe6080604052600436106100a75760003560e01c806384269ed91161006457806384269ed9146105225780639424e63e146105bb578063961237b2146105c5578063a7e94542146105fc578063dd62ed3e14610675578063f8b2cb4f146106fa576100a7565b806311d7a49c146100ac57806359c37501146101255780635ecf53cd14610196578063669e0d391461026b57806377e995cf1461035057806380097484146104a9575b600080fd5b3480156100b857600080fd5b5061010b600480360360408110156100cf57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803563ffffffff16906020019092919050505061076b565b604051808215151515815260200191505060405180910390f35b34801561013157600080fd5b506101746004803603602081101561014857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506107ea565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b3480156101a257600080fd5b50610269600480360360408110156101b957600080fd5b81019080803590602001906401000000008111156101d657600080fd5b8201836020820111156101e857600080fd5b8035906020019184602083028401116401000000008311171561020a57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f820116905080830192505050505050509192919290803563ffffffff169060200190929190505050610843565b005b34801561027757600080fd5b5061034e6004803603606081101561028e57600080fd5b81019080803590602001906401000000008111156102ab57600080fd5b8201836020820111156102bd57600080fd5b803590602001918460208302840111640100000000831117156102df57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f820116905080830192505050505050509192919290803563ffffffff169060200190929190803563ffffffff16906020019092919050505061092f565b005b34801561035c57600080fd5b506104a76004803603604081101561037357600080fd5b810190808035906020019064010000000081111561039057600080fd5b8201836020820111156103a257600080fd5b803590602001918460208302840111640100000000831117156103c457600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f8201169050808301925050505050505091929192908035906020019064010000000081111561042457600080fd5b82018360208201111561043657600080fd5b8035906020019184602083028401116401000000008311171561045857600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f820116905080830192505050505050509192919290505050610a8d565b005b3480156104b557600080fd5b50610508600480360360408110156104cc57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803563ffffffff169060200190929190505050610c05565b604051808215151515815260200191505060405180910390f35b34801561052e57600080fd5b506105a16004803603606081101561054557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803563ffffffff169060200190929190505050610e1a565b604051808215151515815260200191505060405180910390f35b6105c3611222565b005b3480156105d157600080fd5b506105da6112c8565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b34801561060857600080fd5b5061065b6004803603604081101561061f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803563ffffffff16906020019092919050505061131f565b604051808215151515815260200191505060405180910390f35b34801561068157600080fd5b506106e46004803603604081101561069857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506113c6565b6040518082815260200191505060405180910390f35b34801561070657600080fd5b506107496004803603602081101561071d57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611463565b604051808263ffffffff1663ffffffff16815260200191505060405180910390f35b600081600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160192506101000a81548163ffffffff021916908363ffffffff1602179055506001905092915050565b6000600760008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff169050919050565b600960009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461089d57600080fd5b60008090505b825181101561092a5781600660008584815181106108bd57fe5b602002602001015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff16021790555080806001019150506108a3565b505050565b600960009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461098957600080fd5b60008090505b8351811015610a875782600660008684815181106109a957fe5b602002602001015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff1602179055508160076000868481518110610a1a57fe5b602002602001015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff160217905550808060010191505061098f565b50505050565b60008090505b8251811015610c00576000828281518110610aaa57fe5b602002602001015160030b1215610b5957818181518110610ac757fe5b602002602001015160066000858481518110610adf57fe5b602002602001015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160392506101000a81548163ffffffff021916908363ffffffff160217905550610bf3565b818181518110610b6557fe5b602002602001015160066000858481518110610b7d57fe5b602002602001015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160192506101000a81548163ffffffff021916908363ffffffff1602179055505b8080600101915050610a93565b505050565b6000808263ffffffff16118015610c775750600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff168263ffffffff1611155b15610e0f57610ce482600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff166114bc90919063ffffffff16565b600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff160217905550610da982600660008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff166114df90919063ffffffff16565b600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff16021790555060019050610e14565b600090505b92915050565b600080600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff16118015610ec3575060008263ffffffff16115b8015610f6757508163ffffffff16600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff1610155b8015610fce57508163ffffffff16600660008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff1610155b156112165761103b82600660008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff166114bc90919063ffffffff16565b600660008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff16021790555061110082600660008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff166114df90919063ffffffff16565b600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff16021790555081600860008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160392506101000a81548163ffffffff021916908363ffffffff1602179055506001905061121b565b600090505b9392505050565b600960149054906101000a900463ffffffff1663ffffffff1634101561124757600080fd5b600960149054906101000a900463ffffffff1663ffffffff16348161126857fe5b04600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff160217905550565b6000600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff16905090565b600081600860003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff1602179055506001905092915050565b6000600860008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff1663ffffffff16905092915050565b6000600660008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900463ffffffff169050919050565b60008263ffffffff168263ffffffff1611156114d457fe5b818303905092915050565b60008082840190508363ffffffff168163ffffffff1610156114fd57fe5b809150509291505056fea265627a7a72315820f8e99f334dfbeb4016f6fdb6a96521bbd912ccbe8289791426a07ed3db0e77fb64736f6c634300050c0032";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BUYENERGYCOIN = "buyEnergyCoin";

    public static final String FUNC_GETBALANCE = "getBalance";

    public static final String FUNC_GETMYOWNBALANCE = "getMyOwnBalance";

    public static final String FUNC_GETSUPPLIERPRODUCTIONCAPACITY = "getSupplierProductionCapacity";

    public static final String FUNC_REGISTERCONSUMERS = "registerConsumers";

    public static final String FUNC_REGISTERPRODUCERS = "registerProducers";

    public static final String FUNC_SETBALANCE = "setBalance";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_UPDATEACCOUNTBALANCES = "updateAccountBalances";

    @Deprecated
    protected EnergyCoin(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected EnergyCoin(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected EnergyCoin(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected EnergyCoin(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner), 
                new org.web3j.abi.datatypes.Address(160, _spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _spender), 
                new org.web3j.abi.datatypes.generated.Uint32(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> buyEnergyCoin(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_BUYENERGYCOIN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> getBalance(String _addr) {
        final Function function = new Function(FUNC_GETBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getMyOwnBalance() {
        final Function function = new Function(FUNC_GETMYOWNBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getSupplierProductionCapacity(String _addr) {
        final Function function = new Function(FUNC_GETSUPPLIERPRODUCTIONCAPACITY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerConsumers(List<String> addresses, BigInteger initialBalance) {
        final Function function = new Function(
                FUNC_REGISTERCONSUMERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(initialBalance)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> registerProducers(List<String> addresses, BigInteger initialBalance, BigInteger productionCapacity) {
        final Function function = new Function(
                FUNC_REGISTERPRODUCERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(initialBalance), 
                new org.web3j.abi.datatypes.generated.Uint32(productionCapacity)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setBalance(String _addr, BigInteger _value) {
        final Function function = new Function(
                FUNC_SETBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _addr), 
                new org.web3j.abi.datatypes.generated.Uint32(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _to), 
                new org.web3j.abi.datatypes.generated.Uint32(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _from), 
                new org.web3j.abi.datatypes.Address(160, _to), 
                new org.web3j.abi.datatypes.generated.Uint32(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateAccountBalances(List<String> winnersAddresses, List<BigInteger> netPaymentData) {
        final Function function = new Function(
                FUNC_UPDATEACCOUNTBALANCES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(winnersAddresses, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int32>(
                        org.web3j.abi.datatypes.generated.Int32.class,
                        org.web3j.abi.Utils.typeMap(netPaymentData, org.web3j.abi.datatypes.generated.Int32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static EnergyCoin load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EnergyCoin(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static EnergyCoin load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EnergyCoin(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static EnergyCoin load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new EnergyCoin(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static EnergyCoin load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new EnergyCoin(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<EnergyCoin> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String name, String symbol, BigInteger decimals, BigInteger totalSupply, BigInteger exchangeRate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name), 
                new org.web3j.abi.datatypes.Utf8String(symbol), 
                new org.web3j.abi.datatypes.generated.Uint32(decimals), 
                new org.web3j.abi.datatypes.generated.Uint32(totalSupply), 
                new org.web3j.abi.datatypes.generated.Uint32(exchangeRate)));
        return deployRemoteCall(EnergyCoin.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<EnergyCoin> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String name, String symbol, BigInteger decimals, BigInteger totalSupply, BigInteger exchangeRate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name), 
                new org.web3j.abi.datatypes.Utf8String(symbol), 
                new org.web3j.abi.datatypes.generated.Uint32(decimals), 
                new org.web3j.abi.datatypes.generated.Uint32(totalSupply), 
                new org.web3j.abi.datatypes.generated.Uint32(exchangeRate)));
        return deployRemoteCall(EnergyCoin.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<EnergyCoin> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String name, String symbol, BigInteger decimals, BigInteger totalSupply, BigInteger exchangeRate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name), 
                new org.web3j.abi.datatypes.Utf8String(symbol), 
                new org.web3j.abi.datatypes.generated.Uint32(decimals), 
                new org.web3j.abi.datatypes.generated.Uint32(totalSupply), 
                new org.web3j.abi.datatypes.generated.Uint32(exchangeRate)));
        return deployRemoteCall(EnergyCoin.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<EnergyCoin> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String name, String symbol, BigInteger decimals, BigInteger totalSupply, BigInteger exchangeRate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name), 
                new org.web3j.abi.datatypes.Utf8String(symbol), 
                new org.web3j.abi.datatypes.generated.Uint32(decimals), 
                new org.web3j.abi.datatypes.generated.Uint32(totalSupply), 
                new org.web3j.abi.datatypes.generated.Uint32(exchangeRate)));
        return deployRemoteCall(EnergyCoin.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
