package SolidityJava;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
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
public class UnifiedPaymentSettlement extends Contract {
    private static final String BINARY = "60806040526064600460006101000a81548163ffffffff021916908363ffffffff16021790555060646004806101000a81548163ffffffff021916908363ffffffff1602179055506064600460086101000a81548163ffffffff021916908363ffffffff16021790555060646004600c6101000a81548163ffffffff021916908363ffffffff1602179055506064600460106101000a81548163ffffffff021916908363ffffffff1602179055506064600460146101000a81548163ffffffff021916908363ffffffff1602179055503480156100db57600080fd5b506105d4806100eb6000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806351aaecd5146100675780637f2cefdc1461009b578063907bd26a146100cf578063964b5c0814610103578063e2c0099114610137578063f6318fd81461016b575b600080fd5b6100996004803603602081101561007d57600080fd5b81019080803563ffffffff16906020019092919050505061019f565b005b6100cd600480360360208110156100b157600080fd5b81019080803563ffffffff169060200190929190505050610264565b005b610101600480360360208110156100e557600080fd5b81019080803563ffffffff169060200190929190505050610302565b005b6101356004803603602081101561011957600080fd5b81019080803563ffffffff16906020019092919050505061039f565b005b6101696004803603602081101561014d57600080fd5b81019080803563ffffffff169060200190929190505050610464565b005b61019d6004803603602081101561018157600080fd5b81019080803563ffffffff169060200190929190505050610501565b005b60008090506000600460089054906101000a900463ffffffff1682026004809054906101000a900463ffffffff16600460109054906101000a900463ffffffff1602600460009054906101000a900463ffffffff166004600c9054906101000a900463ffffffff16020101905080600260008563ffffffff1663ffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160392506101000a81548163ffffffff021916908363ffffffff160217905550505050565b60008090506000600460089054906101000a900463ffffffff168202600460009054906101000a900463ffffffff166004600c9054906101000a900463ffffffff160201905080600260008563ffffffff1663ffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160192506101000a81548163ffffffff021916908363ffffffff160217905550505050565b60008090506000600460089054906101000a900463ffffffff1682026004809054906101000a900463ffffffff16600460109054906101000a900463ffffffff160201905080600260008563ffffffff1663ffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160192506101000a81548163ffffffff021916908363ffffffff160217905550505050565b60008090506000600460089054906101000a900463ffffffff1682026004809054906101000a900463ffffffff16600460109054906101000a900463ffffffff1602600460009054906101000a900463ffffffff166004600c9054906101000a900463ffffffff16020101905080600260008563ffffffff1663ffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160192506101000a81548163ffffffff021916908363ffffffff160217905550505050565b60008090506000600460089054906101000a900463ffffffff1682026004809054906101000a900463ffffffff16600460109054906101000a900463ffffffff160201905080600260008563ffffffff1663ffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160392506101000a81548163ffffffff021916908363ffffffff160217905550505050565b60008090506000600460089054906101000a900463ffffffff168202600460009054906101000a900463ffffffff166004600c9054906101000a900463ffffffff160201905080600260008563ffffffff1663ffffffff16815260200190815260200160002060008282829054906101000a900463ffffffff160392506101000a81548163ffffffff021916908363ffffffff16021790555050505056fea265627a7a7231582089a40c6fa94fafe1816a6ef412944822fcb457dabb61515956a73c422bbec2b364736f6c634300050c0032";

    public static final String FUNC_SETTLEBILATERALMARKETFORCONSUMER = "SettleBilateralMarketForConsumer";

    public static final String FUNC_SETTLEBILATERALMARKETFORPRODUCER = "SettleBilateralMarketForProducer";

    public static final String FUNC_SETTLEPOOLMARKETFORCONSUMER = "SettlePoolMarketForConsumer";

    public static final String FUNC_SETTLEPOOLMARKETFORPRODUCER = "SettlePoolMarketForProducer";

    public static final String FUNC_SETTLEUNIFIEDMARKETFORCONSUMER = "SettleUnifiedMarketForConsumer";

    public static final String FUNC_SETTLEUNIFIEDMARKETFORPRODUCER = "SettleUnifiedMarketForProducer";

    @Deprecated
    protected UnifiedPaymentSettlement(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected UnifiedPaymentSettlement(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected UnifiedPaymentSettlement(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected UnifiedPaymentSettlement(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> SettleBilateralMarketForConsumer(BigInteger consumerID) {
        final Function function = new Function(
                FUNC_SETTLEBILATERALMARKETFORCONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(consumerID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> SettleBilateralMarketForProducer(BigInteger supplierID) {
        final Function function = new Function(
                FUNC_SETTLEBILATERALMARKETFORPRODUCER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(supplierID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> SettlePoolMarketForConsumer(BigInteger consumerID) {
        final Function function = new Function(
                FUNC_SETTLEPOOLMARKETFORCONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(consumerID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> SettlePoolMarketForProducer(BigInteger supplierID) {
        final Function function = new Function(
                FUNC_SETTLEPOOLMARKETFORPRODUCER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(supplierID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> SettleUnifiedMarketForConsumer(BigInteger consumerID) {
        final Function function = new Function(
                FUNC_SETTLEUNIFIEDMARKETFORCONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(consumerID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> SettleUnifiedMarketForProducer(BigInteger supplierID) {
        final Function function = new Function(
                FUNC_SETTLEUNIFIEDMARKETFORPRODUCER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(supplierID)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static UnifiedPaymentSettlement load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new UnifiedPaymentSettlement(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static UnifiedPaymentSettlement load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new UnifiedPaymentSettlement(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static UnifiedPaymentSettlement load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new UnifiedPaymentSettlement(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static UnifiedPaymentSettlement load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new UnifiedPaymentSettlement(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<UnifiedPaymentSettlement> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(UnifiedPaymentSettlement.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<UnifiedPaymentSettlement> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(UnifiedPaymentSettlement.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<UnifiedPaymentSettlement> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(UnifiedPaymentSettlement.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<UnifiedPaymentSettlement> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(UnifiedPaymentSettlement.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
