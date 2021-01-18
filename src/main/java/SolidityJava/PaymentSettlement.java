package SolidityJava;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Int32;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

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
public class PaymentSettlement extends Contract {
    private static final String BINARY = "608060405260006004806101000a81548163ffffffff021916908363ffffffff16021790555034801561003157600080fd5b506040516115013803806115018339818101604052602081101561005457600080fd5b810190808051906020019092919050505080600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505061140b806100f66000396000f3fe608060405234801561001057600080fd5b50600436106100ea5760003560e01c8063970539091161008c578063dc366ca411610066578063dc366ca4146105b9578063de808087146105c3578063e76d038c14610622578063f8390e5f14610656576100ea565b8063970539091461039f578063a13da0f5146103fe578063bcce8a881461055a576100ea565b80634757492c116100c85780634757492c146102a35780636253a298146102d7578063646e8ec0146103365780636e9ab12914610340576100ea565b8063035bc4d1146100ef57806315d58cb51461024b5780631b080d2414610277575b600080fd5b6102496004803603606081101561010557600080fd5b810190808035906020019064010000000081111561012257600080fd5b82018360208201111561013457600080fd5b8035906020019184602083028401116401000000008311171561015657600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f820116905080830192505050505050509192919290803590602001906401000000008111156101b657600080fd5b8201836020820111156101c857600080fd5b803590602001918460208302840111640100000000831117156101ea57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f820116905080830192505050505050509192919290803563ffffffff1690602001909291905050506106b5565b005b610253610751565b6040518082600181111561026357fe5b60ff16815260200191505060405180910390f35b61027f610768565b6040518082600181111561028f57fe5b60ff16815260200191505060405180910390f35b6102d5600480360360208110156102b957600080fd5b81019080803563ffffffff16906020019092919050505061077f565b005b6102df6107e0565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b83811015610322578082015181840152602081019050610307565b505050509050019250505060405180910390f35b61033e61086e565b005b610348610941565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561038b578082015181840152602081019050610370565b505050509050019250505060405180910390f35b6103a76109c5565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156103ea5780820151818401526020810190506103cf565b505050509050019250505060405180910390f35b6105586004803603606081101561041457600080fd5b810190808035906020019064010000000081111561043157600080fd5b82018360208201111561044357600080fd5b8035906020019184602083028401116401000000008311171561046557600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f820116905080830192505050505050509192919290803590602001906401000000008111156104c557600080fd5b8201836020820111156104d757600080fd5b803590602001918460208302840111640100000000831117156104f957600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f820116905080830192505050505050509192919290803563ffffffff169060200190929190505050610a53565b005b610562610aa7565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156105a557808201518184015260208101905061058a565b505050509050019250505060405180910390f35b6105c1610b2b565b005b6105cb611023565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561060e5780820151818401526020810190506105f3565b505050509050019250505060405180910390f35b6106546004803603602081101561063857600080fd5b81019080803563ffffffff1690602001909291905050506110a1565b005b61065e611102565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156106a1578082015181840152602081019050610686565b505050509050019250505060405180910390f35b6000600b60006101000a81548160ff021916908360018111156106d457fe5b02179055506000600b60016101000a81548160ff021916908360018111156106f857fe5b021790555080600460006101000a81548163ffffffff021916908363ffffffff1602179055508160099080519060200190610734929190611190565b50826005908051906020019061074b929190611240565b50505050565b6000600b60019054906101000a900460ff16905090565b6000600b60009054906101000a900460ff16905090565b80600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff16021790555050565b6060600680548060200260200160405190810160405280929190818152602001828054801561086457602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001906001019080831161081a575b5050505050905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146108c757600080fd5b6001600b60016101000a81548160ff021916908360018111156108e657fe5b0217905550600560006108f991906112ca565b6006600061090791906112ca565b6007600061091591906112eb565b6008600061092391906112ca565b600960006109319190611313565b600a600061093f9190611313565b565b6060600a8054806020026020016040519081016040528092919081815260200182805480156109bb57602002820191906000526020600020906000905b82829054906101000a900463ffffffff1663ffffffff168152602001906004019060208260030104928301926001038202915080841161097e5790505b5050505050905090565b60606008805480602002602001604051908101604052809291908181526020018280548015610a4957602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190600101908083116109ff575b5050505050905090565b80600460006101000a81548163ffffffff021916908363ffffffff16021790555081600a9080519060200190610a8a929190611190565b508260069080519060200190610aa1929190611240565b50505050565b60606009805480602002602001604051908101604052809291908181526020018280548015610b2157602002820191906000526020600020906000905b82829054906101000a900463ffffffff1663ffffffff1681526020019060040190602082600301049283019260010382029150808411610ae45790505b5050505050905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610b8457600080fd5b6001600b60006101000a81548160ff02191690836001811115610ba357fe5b021790555060008090505b600680549050811015610d2c57600060068281548110610bca57fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506000600a8381548110610c0657fe5b90600052602060002090600891828204019190066004029054906101000a900463ffffffff169050600080905060006004809054906101000a900463ffffffff168202600460009054906101000a900463ffffffff168402019050600781908060018154018082558091505090600182039060005260206000209060089182820401919006600402909192909190916101000a81548163ffffffff021916908360030b63ffffffff1602179055505060088490806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050505050508080600101915050610bae565b5060008090505b600580549050811015610eb157600060058281548110610d4f57fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600060098381548110610d8b57fe5b90600052602060002090600891828204019190066004029054906101000a900463ffffffff169050600080905060006004809054906101000a900463ffffffff168202600460009054906101000a900463ffffffff168402039050600781908060018154018082558091505090600182039060005260206000209060089182820401919006600402909192909190916101000a81548163ffffffff021916908360030b63ffffffff1602179055505060088490806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050505050508080600101915050610d33565b50600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166377e995cf600860076040518363ffffffff1660e01b81526004018080602001806020018381038352858181548152602001915080548015610f8257602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019060010190808311610f38575b50508381038252848181548152602001915080548015610fe757602002820191906000526020600020906000905b82829054906101000a900460030b60030b81526020019060040190602082600301049283019260010382029150808411610fb05790505b5050945050505050600060405180830381600087803b15801561100957600080fd5b505af115801561101d573d6000803e3d6000fd5b50505050565b6060600780548060200260200160405190810160405280929190818152602001828054801561109757602002820191906000526020600020906000905b82829054906101000a900460030b60030b815260200190600401906020826003010492830192600103820291508084116110605790505b5050505050905090565b80600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548163ffffffff021916908363ffffffff16021790555050565b6060600580548060200260200160405190810160405280929190818152602001828054801561118657602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001906001019080831161113c575b5050505050905090565b8280548282559060005260206000209060070160089004810192821561122f5791602002820160005b838211156111fd57835183826101000a81548163ffffffff021916908363ffffffff16021790555092602001926004016020816003010492830192600103026111b9565b801561122d5782816101000a81549063ffffffff02191690556004016020816003010492830192600103026111fd565b505b50905061123c919061133b565b5090565b8280548282559060005260206000209081019282156112b9579160200282015b828111156112b85782518260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555091602001919060010190611260565b5b5090506112c6919061136e565b5090565b50805460008255906000526020600020908101906112e891906113b1565b50565b50805460008255600701600890049060005260206000209081019061131091906113b1565b50565b50805460008255600701600890049060005260206000209081019061133891906113b1565b50565b61136b91905b8082111561136757600081816101000a81549063ffffffff021916905550600101611341565b5090565b90565b6113ae91905b808211156113aa57600081816101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905550600101611374565b5090565b90565b6113d391905b808211156113cf5760008160009055506001016113b7565b5090565b9056fea265627a7a723158203c7958ac43fd59019fe50be6f643f308aa3b68d0f774bf913d204618680a589f64736f6c634300050c0032";

    public static final String FUNC_SETTLEPAYMENT = "SettlePayment";

    public static final String FUNC_GETALLNETPAYMENT = "getAllNetPayment";

    public static final String FUNC_GETALLPOOLMARKETWINNERS = "getAllPoolMarketWinners";

    public static final String FUNC_GETPAYMENTSETTLEMENTRESETTINGSTATE = "getPaymentSettlementResettingState";

    public static final String FUNC_GETPAYMENTSETTLEMENTSTATE = "getPaymentSettlementState";

    public static final String FUNC_GETPOOLMARKETCONSUMERENERGYDISPATCH = "getPoolMarketConsumerEnergyDispatch";

    public static final String FUNC_GETPOOLMARKETCONSUMERWINNERS = "getPoolMarketConsumerWinners";

    public static final String FUNC_GETPOOLMARKETSUPPLIERENERGYDISPATCH = "getPoolMarketSupplierEnergyDispatch";

    public static final String FUNC_GETPOOLMARKETSUPPLIERWINNERS = "getPoolMarketSupplierWinners";

    public static final String FUNC_POOLMARKETCONSUMERSDISPATCHDATA = "poolMarketConsumersDispatchData";

    public static final String FUNC_POOLMARKETSUPPLIERDISPATCHDATA = "poolMarketSupplierDispatchData";

    public static final String FUNC_RESETPAYMENTSETTLEMENT = "resetPaymentSettlement";

    public static final String FUNC_SMARTMETERCONSUMEDENERGY = "smartMeterConsumedEnergy";

    public static final String FUNC_SMARTMETERPRODUCEDENERGY = "smartMeterProducedEnergy";

    @Deprecated
    protected PaymentSettlement(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PaymentSettlement(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PaymentSettlement(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PaymentSettlement(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> SettlePayment() {
        final Function function = new Function(
                FUNC_SETTLEPAYMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getAllNetPayment() {
        final Function function = new Function(FUNC_GETALLNETPAYMENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Int32>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getAllPoolMarketWinners() {
        final Function function = new Function(FUNC_GETALLPOOLMARKETWINNERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getPaymentSettlementResettingState() {
        final Function function = new Function(FUNC_GETPAYMENTSETTLEMENTRESETTINGSTATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getPaymentSettlementState() {
        final Function function = new Function(FUNC_GETPAYMENTSETTLEMENTSTATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getPoolMarketConsumerEnergyDispatch() {
        final Function function = new Function(FUNC_GETPOOLMARKETCONSUMERENERGYDISPATCH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint32>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getPoolMarketConsumerWinners() {
        final Function function = new Function(FUNC_GETPOOLMARKETCONSUMERWINNERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getPoolMarketSupplierEnergyDispatch() {
        final Function function = new Function(FUNC_GETPOOLMARKETSUPPLIERENERGYDISPATCH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint32>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getPoolMarketSupplierWinners() {
        final Function function = new Function(FUNC_GETPOOLMARKETSUPPLIERWINNERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> poolMarketConsumersDispatchData(List<String> _poolMarketConsumerWinners, List<BigInteger> _poolMarketConsumerEnergyDispatch, BigInteger _poolMarketClearingPrice) {
        final Function function = new Function(
                FUNC_POOLMARKETCONSUMERSDISPATCHDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_poolMarketConsumerWinners, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint32>(
                        org.web3j.abi.datatypes.generated.Uint32.class,
                        org.web3j.abi.Utils.typeMap(_poolMarketConsumerEnergyDispatch, org.web3j.abi.datatypes.generated.Uint32.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(_poolMarketClearingPrice)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> poolMarketSupplierDispatchData(List<String> _poolMarketSupplierWinners, List<BigInteger> _poolMarketSupplierEnergyDispatch, BigInteger _poolMarketClearingPrice) {
        final Function function = new Function(
                FUNC_POOLMARKETSUPPLIERDISPATCHDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_poolMarketSupplierWinners, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint32>(
                        org.web3j.abi.datatypes.generated.Uint32.class,
                        org.web3j.abi.Utils.typeMap(_poolMarketSupplierEnergyDispatch, org.web3j.abi.datatypes.generated.Uint32.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(_poolMarketClearingPrice)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> resetPaymentSettlement() {
        final Function function = new Function(
                FUNC_RESETPAYMENTSETTLEMENT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> smartMeterConsumedEnergy(BigInteger consumedAmount) {
        final Function function = new Function(
                FUNC_SMARTMETERCONSUMEDENERGY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(consumedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> smartMeterProducedEnergy(BigInteger producedAmount) {
        final Function function = new Function(
                FUNC_SMARTMETERPRODUCEDENERGY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(producedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PaymentSettlement load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PaymentSettlement(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PaymentSettlement load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PaymentSettlement(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PaymentSettlement load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PaymentSettlement(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PaymentSettlement load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PaymentSettlement(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<PaymentSettlement> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _energyCoinAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _energyCoinAddress)));
        return deployRemoteCall(PaymentSettlement.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<PaymentSettlement> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _energyCoinAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _energyCoinAddress)));
        return deployRemoteCall(PaymentSettlement.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PaymentSettlement> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _energyCoinAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _energyCoinAddress)));
        return deployRemoteCall(PaymentSettlement.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PaymentSettlement> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _energyCoinAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _energyCoinAddress)));
        return deployRemoteCall(PaymentSettlement.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
