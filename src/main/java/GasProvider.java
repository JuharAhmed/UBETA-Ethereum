import SolidityJava.EnergyCoin;
import SolidityJava.PaymentSettlement;
import SolidityJava.PoolMarket;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

public class GasProvider implements ContractGasProvider {
    @Override
    public BigInteger getGasPrice(String contractFunc) {
        switch (contractFunc) {
            case PoolMarket.FUNC_INITIALIZEBIDDING: return BigInteger.valueOf(22_000_000L);
            case PoolMarket.FUNC_CALCULATEMARKERCLEARINGPRICE: return BigInteger.valueOf(22_000_000_000L);
            case PoolMarket.FUNC_SENDDATAANDRESETPOOLMARKET: return BigInteger.valueOf(22_000_000_00L);
            case PoolMarket.FUNC_SUBMITENERGYBID:return BigInteger.valueOf(22_000_000L);
            case PoolMarket.FUNC_SUBMITENERGYOFFER:return BigInteger.valueOf(22_000_000L);
            case PoolMarket.FUNC_DEPLOY:return BigInteger.valueOf(22_000_000L);
            case PoolMarket.FUNC_SENDDATATOPAYMENTSETTLEMENT:return BigInteger.valueOf(22_000_000L);

            case PaymentSettlement.FUNC_POOLMARKETCONSUMERSDISPATCHDATA:return BigInteger.valueOf(22_000_000L);
            case PaymentSettlement.FUNC_POOLMARKETSUPPLIERDISPATCHDATA:return BigInteger.valueOf(22_000_000L);
            case PaymentSettlement.FUNC_SETTLEPAYMENT:return BigInteger.valueOf(22_000_000L);
            case PaymentSettlement.FUNC_RESETPAYMENTSETTLEMENT:return BigInteger.valueOf(22_000_000L);
            case PaymentSettlement.FUNC_SMARTMETERCONSUMEDENERGY:return BigInteger.valueOf(22_000_000L);
            case PaymentSettlement.FUNC_SMARTMETERPRODUCEDENERGY:return BigInteger.valueOf(22_000_000L);

            case EnergyCoin.FUNC_REGISTERCONSUMERS:return BigInteger.valueOf(22_000_000_00L);
            case EnergyCoin.FUNC_REGISTERPRODUCERS:return BigInteger.valueOf(22_000_000_00L);
            case EnergyCoin.FUNC_UPDATEACCOUNTBALANCES:return BigInteger.valueOf(22_000_000_00L);
            case EnergyCoin.FUNC_TRANSFER:return BigInteger.valueOf(22_000_000L);
            case EnergyCoin.FUNC_TRANSFERFROM:return BigInteger.valueOf(22_000_000L);
            case EnergyCoin.FUNC_ALLOWANCE:return BigInteger.valueOf(22_000_000L);
            case EnergyCoin.FUNC_APPROVE:return BigInteger.valueOf(22_000_000L);
            case EnergyCoin.FUNC_BUYENERGYCOIN:return BigInteger.valueOf(22_000_000L);
            default:
                    return BigInteger.valueOf(22_000_000L);
        }

    }

    @Override
    public BigInteger getGasPrice() {
        return null;
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        switch (contractFunc) {
            case PoolMarket.FUNC_INITIALIZEBIDDING: return BigInteger.valueOf(1_000_000_000L);
            case PoolMarket.FUNC_CALCULATEMARKERCLEARINGPRICE: return BigInteger.valueOf(22_000_000_000L); //This is Just the maximum amount of gaz limit set in the genesis
            case PoolMarket.FUNC_SENDDATAANDRESETPOOLMARKET: return BigInteger.valueOf(1_000_000_000L); //This is Just the maximum amount of gaz limit set in the genesis
            case PoolMarket.FUNC_SUBMITENERGYBID:return BigInteger.valueOf(1_000_000_000L);
            case PoolMarket.FUNC_SUBMITENERGYOFFER:return BigInteger.valueOf(1_000_000_000L);
            case PoolMarket.FUNC_SENDDATATOPAYMENTSETTLEMENT:return BigInteger.valueOf(1_000_000_000L);

            case PaymentSettlement.FUNC_SETTLEPAYMENT:return BigInteger.valueOf(1_000_000_000L);
            case PaymentSettlement.FUNC_RESETPAYMENTSETTLEMENT:return BigInteger.valueOf(1_000_000_000L);
            case PaymentSettlement.FUNC_POOLMARKETCONSUMERSDISPATCHDATA:return BigInteger.valueOf(1_000_000_000L);
            case PaymentSettlement.FUNC_POOLMARKETSUPPLIERDISPATCHDATA:return BigInteger.valueOf(1_000_000_000L);
            case PaymentSettlement.FUNC_SMARTMETERCONSUMEDENERGY:return BigInteger.valueOf(1_000_000_000L);
            case PaymentSettlement.FUNC_SMARTMETERPRODUCEDENERGY:return BigInteger.valueOf(1_000_000L);

            case EnergyCoin.FUNC_REGISTERCONSUMERS:return BigInteger.valueOf(1_000_000_000L);
            case EnergyCoin.FUNC_REGISTERPRODUCERS:return BigInteger.valueOf(1_000_000_000L);
            case EnergyCoin.FUNC_UPDATEACCOUNTBALANCES:return BigInteger.valueOf(1_000_000_000L);
            case EnergyCoin.FUNC_TRANSFER:return BigInteger.valueOf(1_000_000_000L);
            case EnergyCoin.FUNC_TRANSFERFROM:return BigInteger.valueOf(1_000_000_000L);
            case EnergyCoin.FUNC_ALLOWANCE:return BigInteger.valueOf(1_000_000_000L);
            case EnergyCoin.FUNC_APPROVE:return BigInteger.valueOf(1_000_000_000L);
            case EnergyCoin.FUNC_BUYENERGYCOIN:return BigInteger.valueOf(1_000_000_000L);
            default:
                    return BigInteger.valueOf(1_000_000L);
        }
    }

    @Override
    public BigInteger getGasLimit() {
        return null;
    }
}
