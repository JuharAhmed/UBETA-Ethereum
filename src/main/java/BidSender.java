import SolidityJava.PoolMarket;
import okhttp3.OkHttpClient;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BidSender implements Runnable {
    SmartMeter smartMeter;
    ArrayList<Transaction> transactionList = new ArrayList<>();
    private Transaction transaction;
    private long submissionTime;
    private long responseTime;
    private PoolMarket poolMarketContract;
    BigInteger energyAmount;
    BigInteger priceRate;
    int round;
    int ID;
    Credentials credentials;
    ArrayList<String> urls;
    private Web3j web3j;
    long sendingTime;

    public BidSender(long sendingTime, SmartMeter smartMeter, Credentials credentials, ArrayList<String> urls, int round, int consumerID, BigInteger energyAmount, BigInteger priceRate){
    this.ID=consumerID;
    this.energyAmount=energyAmount;
    this.priceRate=priceRate;
    this.smartMeter=smartMeter;
    this.round=round;
    this.credentials = credentials;
    this.urls=urls;
    this.sendingTime=sendingTime;
    }
    @Override
    public void run() {

        try {
            Thread.sleep(sendingTime-System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(10, TimeUnit.MINUTES) // write timeout
                .readTimeout(10, TimeUnit.MINUTES); // read timeout
        OkHttpClient okHttpClient = builder.build();

        Random rand = new Random();
        int randomNode = rand.nextInt(urls.size()-1); //The first two nodes are allocated for DSO and Monitor
        //web3j=Web3j.build(new HttpService(urls.get(randomNode +1)),1000, Async.defaultExecutorService() );
        //web3j=Web3j.build(new HttpService(urls.get(randomNode +1)));
        web3j=Web3j.build(new HttpService(urls.get(randomNode +1), okHttpClient,false));

      //  poolMarketContract= PoolMarket.load(MainJavaClass.POOL_MARKET_CONTRACT_ADDRESS,web3j,this.credentials,new GasProvider());

        poolMarketContract= PoolMarket.load(MainJavaClass.POOL_MARKET_CONTRACT_ADDRESS,web3j,this.credentials,new GasProvider());
        try {
      // System.out.println("Consumer: "+ID+" Bidding State: "+poolMarketContract.getState().send().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        TransactionReceipt transactionReceipt = null;
        try {
                submissionTime=System.currentTimeMillis();
                transactionReceipt=poolMarketContract.submitEnergyBid(this.energyAmount,this.priceRate).send();
                responseTime=System.currentTimeMillis();
                    transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PM_Bidding,
                            round, Constants.TransactionStatus.SUCCESS,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());

            } catch (Exception e) {
            responseTime=System.currentTimeMillis();
            // e.printStackTrace();
            //  System.err.println(" Producer: "+ID+ " Failed to send Offer");
            if(e instanceof  org.web3j.protocol.exceptions.TransactionException){
                System.err.println("Round: "+round+ " Consumer: "+ID+" Transaction Passed 600 Seconds, Duration: "+ (responseTime-submissionTime));
            }
            else if (e instanceof java.net.SocketTimeoutException || e instanceof java.net.SocketException){
                System.err.println("Round: "+round+" Consumer: "+ID+" Socket Time Out, Duration: "+ (responseTime-submissionTime));
            }
            else {
                System.err.println("Round: "+round+" Consumer: "+ID+" Other Failure, Duration: "+ (responseTime-submissionTime));
                e.printStackTrace();
            }
            transaction =new Transaction(null,Constants.TransactionType.PM_Bidding,
                    round, Constants.TransactionStatus.FAILED,submissionTime,responseTime,BigInteger.ZERO,BigInteger.ZERO);
            // System.out.println("Producer "+ ID + " Energy Bid Submitted for round " +round+ " Amount: "+this.energyAmount+" Price: "+priceRate);
            }
        web3j.shutdown();
        Report.pushTransactionReport(transaction);
        
    }
}
