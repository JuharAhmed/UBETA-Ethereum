import SolidityJava.PoolMarket;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Async;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

public class QuerySenderDispatchedEneergy extends TimerTask {

    private PoolMarket poolMarketContract;
    private QueryTransaction transaction;
    private long submissionTime;
    private long responseTime;
    int round;
    int ID;
    Credentials credentials;
    SmartMeter smartMeter;
    Web3j web3j;
    ArrayList<String> urls;

    public QuerySenderDispatchedEneergy(SmartMeter smartMeter, Credentials credentials, ArrayList<String> urls, int round, int ID){
        this.ID=ID;
        this.round=round;
        this.credentials = credentials;
        this.urls=urls;
        this.smartMeter=smartMeter;
    }

    @Override
    public void run() {
        Random rand = new Random();
        int randomNode = rand.nextInt(urls.size()-1); //The first two nodes are allocated for DSO and Monitor
        web3j=Web3j.build(new HttpService(urls.get(randomNode +1)),1000, Async.defaultExecutorService() );
        poolMarketContract= PoolMarket.load(MainJavaClass.POOL_MARKET_CONTRACT_ADDRESS,web3j,this.credentials,new GasProvider());
       // poolMarketContract= PoolMarket.load(MainJavaClass.POOL_MARKET_CONTRACT_ADDRESS, web3j,this.credentials,new GasProvider());
        BigInteger dispatchedEnergy = null;
        try {
            submissionTime=System.currentTimeMillis();
            if(this.smartMeter.role==Constants.Role.CONSUMER){
                dispatchedEnergy=poolMarketContract.getConsumerDispatchedEnergy().send();
               // System.out.println("Consumer: "+ID+" Dispatched Energy: "+dispatchedEnergy);
            }
            else{
                dispatchedEnergy=poolMarketContract.getSupplierDispatchedEnergy().send();
               // System.out.println("Supplier: "+ID+" Dispatched Energy: "+dispatchedEnergy);
            }
            responseTime=System.currentTimeMillis();
            transaction =new QueryTransaction(Constants.TransactionType.PM_getDispachedEnergy,Constants.TransactionStatus.SUCCESS,round,submissionTime,responseTime);
        } catch (Exception e) {
            System.err.println(" Energy Dipatch Query: "+ID+ " Failed ");
            transaction =new QueryTransaction(Constants.TransactionType.PM_getDispachedEnergy,Constants.TransactionStatus.FAILED,round,submissionTime,responseTime);
        }

        Report.pushQueryTransactionReport(transaction);

    }
}
