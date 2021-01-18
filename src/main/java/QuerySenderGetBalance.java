import SolidityJava.EnergyCoin;
import okhttp3.OkHttpClient;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class QuerySenderGetBalance implements Runnable {

    private EnergyCoin energyCoinContract;
    private QueryTransaction transaction;
    private long submissionTime;
    private long responseTime;
    int round;
    int ID;
    Credentials credentials;
    SmartMeter smartMeter;
    Web3j web3j;
    ArrayList<String> urls;
    long sendingTime;

    public QuerySenderGetBalance(long sendingTime, SmartMeter smartMeter, Credentials credentials, ArrayList<String> urls, int round, int ID){
        this.ID=ID;
        this.round=round;
        this.credentials = credentials;
        this.urls=urls;
        this.smartMeter=smartMeter;
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
        energyCoinContract= EnergyCoin.load(MainJavaClass.EnergyCoin_CONTRACT_ADDRESS,web3j,this.credentials,new GasProvider());
        BigInteger balance = null;
        try {
            submissionTime=System.currentTimeMillis();
                balance=energyCoinContract.getMyOwnBalance().send();
              //  System.out.println("Supplier: "+ID+" Balance: "+balance);
        responseTime=System.currentTimeMillis();
            transaction =new QueryTransaction(Constants.TransactionType.PS_getBalance,Constants.TransactionStatus.SUCCESS,round,submissionTime,responseTime);
        } catch (Exception e) {
            responseTime=System.currentTimeMillis();
            System.err.println(" Get Balance Query: "+ID+ " Failed ");
            transaction =new QueryTransaction(Constants.TransactionType.PS_getBalance,Constants.TransactionStatus.FAILED,round,submissionTime,responseTime);
        }
        web3j.shutdown();
        Report.pushQueryTransactionReport(transaction);
        if(this.smartMeter.role== Constants.Role.CONSUMER)
        System.err.println(" Consumer: "+ID+ "Get Balance for round "+round );
        else
        System.err.println(" Producer: "+ID+ "Get Balance for round "+round );

    }
}
