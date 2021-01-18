import SolidityJava.EnergyCoin;
import SolidityJava.PaymentSettlement;
import SolidityJava.PoolMarket;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DSO implements Runnable{
    private final Web3j web3j;
    private final Credentials credentials;
    private final String accountAddress;
    String privateKey;
    String address;
    long delay= 30000;//1 seconds delay expressed in milli seconds
    private PoolMarket poolMarketContract;
    private PaymentSettlement paymentSettlementContract;
    private EnergyCoin energyCoinContract;
    private final Object lock = new Object();
    GasProvider gasProvider= new GasProvider();
    TestSchedule testSchedule;
    private long currentTime;
    ArrayList<Transaction> transactionList = new ArrayList<>();
    private Transaction transaction;
    private long submissionTime;
    private long responseTime;
    private BigInteger marketClearingPrice;
    private Object marketCleared=new Object();
    private ArrayList<Task> DSORoundTasks;

    DSO (TestSchedule testSchedule, Web3j web3j, String privateKey) {
        this.testSchedule=testSchedule;
        this.web3j=web3j;
        this.credentials = Credentials.create(privateKey);
        this.privateKey= credentials.getEcKeyPair().getPrivateKey().toString(16);
        this.accountAddress=this.credentials.getAddress();
    }

    @Override
    public void run() {
        poolMarketContract= PoolMarket.load(MainJavaClass.POOL_MARKET_CONTRACT_ADDRESS,web3j,this.credentials,this.gasProvider);
        paymentSettlementContract=PaymentSettlement.load(MainJavaClass.PaymentSettlement_CONTRACT_ADDRESS,web3j,this.credentials,this.gasProvider);
        energyCoinContract=EnergyCoin.load(MainJavaClass.EnergyCoin_CONTRACT_ADDRESS,web3j,this.credentials,this.gasProvider);
        long numberOfRounds=testSchedule.numberOfRounds;
        int round=0;
        long waitingTime=0;
        Task task;

        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2);
        while(round <numberOfRounds){
            DSORoundTasks=getDSORoundTasks(round);
            System.out.println("DSO: Starting round: "+round);

            for(int ts=0;ts<DSORoundTasks.size();ts++){
                task=DSORoundTasks.get(ts);
                System.out.println("DSO: Round "+round+" task "+task.taskType);
                waitingTime=task.startTime-System.currentTimeMillis();
               System.out.println("DSO: Waiting for "+waitingTime/1000+" Seconds Until round "+round+" "+task.taskType+ " Time is Reached ");
                try {
                    if(waitingTime>1000)
                     Thread.sleep(waitingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executor.schedule(new DSOWorkerThread(web3j,credentials,task,round,poolMarketContract,paymentSettlementContract,energyCoinContract),0, TimeUnit.MILLISECONDS);
                try {
                    Thread.sleep(MainJavaClass.shortIntervalDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!taskSucessful(task)){
                    executor.schedule(new DSOWorkerThread(web3j,credentials,task,round,poolMarketContract,paymentSettlementContract,energyCoinContract),0, TimeUnit.MILLISECONDS);
                }
                else{
                    continue;
                }

/*                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!taskSucessful(task)){
                    executor.schedule(new DSOWorkerThread(web3j,credentials,task,round,poolMarketContract,paymentSettlementContract,energyCoinContract),0, TimeUnit.MILLISECONDS);
                }

               try {
                    if(waitingTime >500)
                    Thread.sleep(waitingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executeTask(task, round);*/
            }

            System.out.println("DSO: Finishing round: "+round);
           //Current Round Finished increment round
           round++;

       }
        // Waiting until worker Threads finish
        waitingTime=testSchedule.endTime-System.currentTimeMillis();
        try {
            if(waitingTime>1000)
            Thread.sleep(waitingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean taskSucessful(Task task) {
    boolean taskSuccessful=false;
        switch (task.taskType){
            case BiM_Initialization:
                break;
            case BiM_Resetting:
                break;
            case PM_Initialization:
                try {
                    System.out.println("Bidding Initialization State: "+poolMarketContract.getBiddingState().send().toString());
                    if(poolMarketContract.getBiddingState().send().toString().equals("1")){
                        taskSuccessful=true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PM_MarketClearance:
                try {
                    System.out.println("Market Clearance State: "+poolMarketContract.getMarketClearanceState().send().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                   if(poolMarketContract.getMarketClearanceState().send().toString().equals("1"))
                    taskSuccessful=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PM_SendingDataAndMarketResetting:
                try {
                    if(poolMarketContract.getMarketResettingState().send().toString().equals("1"))
                    taskSuccessful=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case BaM_CalculateMismatch:
                break;
            case BaM_MarketClearance:
                break;
            case BaM_MarketResetting:
                break;
            case PS_SettlingPayments:
                try {
                    System.out.println("Payment Settlement State: "+paymentSettlementContract.getPaymentSettlementState().send().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if(paymentSettlementContract.getPaymentSettlementState().send().toString().equals("1"))
                    taskSuccessful=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PS_Resetting:
                try {
                    if(paymentSettlementContract.getPaymentSettlementResettingState().send().toString().equals("1"))
                    taskSuccessful=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }                break;
            default:
                System.err.println("DSO: Task Type not recognized");
                break;
        }
        return  taskSuccessful;

    }


    protected static class DSOWorkerThread implements Runnable {
        Credentials credentials;
        Web3j web3j;
        Task task;
        private PoolMarket poolMarketContract;
        private PaymentSettlement paymentSettlementContract;
        private EnergyCoin energyCoinContract;
        int round;
        long submissionTime;
        long responseTime;
        Transaction transaction;

        public DSOWorkerThread (Web3j web3j, Credentials credentials, Task task, int round, PoolMarket poolMarketContract, PaymentSettlement paymentSettlementContract,EnergyCoin energyCoinContract){
            this.web3j=web3j;
            this.credentials = credentials;
           this.task=task;
           this.poolMarketContract=poolMarketContract;
           this.paymentSettlementContract=paymentSettlementContract;
           this.energyCoinContract=energyCoinContract;
           this.round=round;

        }
        @Override
        public void run() {
            switch (task.taskType){
                case BiM_Initialization:
                    break;
                case BiM_Resetting:
                    break;
                case PM_Initialization:
                    initializeBidding(round);
                    break;
                case PM_MarketClearance:
                    clearMarket(round);
                    break;
                case PM_SendingDataAndMarketResetting:
                    resetPoolMarket(round);
                    break;
                case BaM_CalculateMismatch:
                    break;
                case BaM_MarketClearance:
                    break;
                case BaM_MarketResetting:
                    break;
                case PS_SettlingPayments:
                    settlePayments(round);
                    break;
                case PS_Resetting:
                    resetPaymentSettlement(round);
                    break;
                default:
                    System.err.println("DSO: Task Type not recognized");
                    break;
            }

        }

        private void initializeBidding(int round) {
            System.out.println("DSO: Initializing Bidding for round "+round);

            try {
                TransactionReceipt transactionReceipt= poolMarketContract.initializeBidding().send();//Just change this to bidding state
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("DSO:  Bidding initialized for round "+round);
        }

        private void clearMarket(int round)  {
            System.out.println("DSO: Starting Market Clearance for round "+round);
            TransactionReceipt transactionReceipt = null;
            try {
                submissionTime=System.currentTimeMillis();
                System.out.println("DOS: Market Clearance Submission Time: "+submissionTime);
                transactionReceipt= poolMarketContract.calculateMarkerClearingPrice().send();
                responseTime=System.currentTimeMillis();
                System.err.println("DSO: Market Clearance: Time Between Submission and Response: "+(responseTime-submissionTime));
                if(transactionReceipt.getStatus().equals("0")){
                    transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PM_MarketClearance,
                            round, Constants.TransactionStatus.FAILED,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
                }
                else {
                    transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PM_MarketClearance,
                            round, Constants.TransactionStatus.SUCCESS,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
                }
                Report.pushTransactionReport(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Market Clearing Price for round " +round+ ":"+poolMarketContract.getMarketClearingPrice().send().doubleValue());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Total Number of Consumers From PM " +round+ ":"+poolMarketContract.getConsumers().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Total Number of Suppliers From PM " +round+ ":"+poolMarketContract.getSuppliers().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Number of Consumer Winners from PM " +round+ ":"+poolMarketContract.getConsumerWinners().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Number of Supplier Winners from PM  " +round+ ":"+poolMarketContract.getSupplierWinners().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void resetPoolMarket(int round) {
            System.out.println("DSO: Resetting Pool Market");
            try {
                TransactionReceipt transactionReceipt= poolMarketContract.SendDataAndResetPoolMarket().send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("DSO:  Pool Market resetting Finished for Round "+round);


            try {
                System.out.println("DSO: Number of Consumer Winners from PS " +round+ ":"+paymentSettlementContract.getPoolMarketConsumerWinners().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Number of Supplier Winners from PS  " +round+ ":"+paymentSettlementContract.getPoolMarketSupplierWinners().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Number of Consumer Dispatches from PS " +round+ ":"+paymentSettlementContract.getPoolMarketConsumerEnergyDispatch().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Number of Supplier Dispatches from PS  " +round+ ":"+paymentSettlementContract.getPoolMarketSupplierEnergyDispatch().send().size());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        private void settlePayments(int round) {
            System.out.println("DSO: Starting Payment Settlement for round "+round);
            try {
                submissionTime=System.currentTimeMillis();
                System.out.println("DOS: Payment Settlement Submission Time: "+submissionTime);
                TransactionReceipt transactionReceipt= paymentSettlementContract.SettlePayment().send();
                responseTime=System.currentTimeMillis();
                System.err.println("DSO: Payment Settlement: Time Between Submission and Response: "+(responseTime-submissionTime));
                if(transactionReceipt.getStatus().equals("0")){
                    transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PS_SettlingPayments,
                            round, Constants.TransactionStatus.FAILED,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
                }
                else {
                    transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PS_SettlingPayments,
                            round, Constants.TransactionStatus.SUCCESS,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
                }
                Report.pushTransactionReport(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("DSO: Payment Settlement Finished for round " +round);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void resetPaymentSettlement(int round) {
            System.out.println("DSO: Resetting Payment Settlement");
            try {
                TransactionReceipt transactionReceipt= paymentSettlementContract.resetPaymentSettlement().send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("DSO:  Payment Resetting Finished for Round "+round);
        }

    }

    private void executeTask(Task task, int round ) {
        switch (task.taskType){
            case BiM_Initialization:
                break;
            case BiM_Resetting:
                break;
            case PM_Initialization:
                initializeBidding(round);
                break;
            case PM_MarketClearance:
                clearMarket(round);
                break;
            case PM_SendingDataAndMarketResetting:
                resetPoolMarket(round);
                break;
            case BaM_CalculateMismatch:
                break;
            case BaM_MarketClearance:
                break;
            case BaM_MarketResetting:
                break;
            case PS_SettlingPayments:
                settlePayments(round);
                break;
            case PS_Resetting:
                resetPaymentSettlement(round);
                break;
            default:
                System.err.println("DSO: Task Type not recognized");
                break;
        }
    }

    private void sendDataToPS(int round) {
       System.out.println("DSO: Sending Data to Payment Settlement for round "+round);
        try {
            TransactionReceipt transactionReceipt= poolMarketContract.sendDataToPaymentSettlement().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DSO: Sending Data to PS Finished for Round "+round);

    }

    private void resetPaymentSettlement(int round) {
        System.out.println("DSO: Resetting Payment Settlement");
        try {
            TransactionReceipt transactionReceipt= paymentSettlementContract.resetPaymentSettlement().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DSO:  Payment Resetting Finished for Round "+round);
    }

    private void settlePayments(int round) {
        System.out.println("DSO: Starting Payment Settlement for round "+round);
        try {
            submissionTime=System.currentTimeMillis();
            System.out.println("DOS: Payment Settlement Submission Time: "+submissionTime);
            TransactionReceipt transactionReceipt= paymentSettlementContract.SettlePayment().send();
            responseTime=System.currentTimeMillis();
            System.err.println("DSO: Payment Settlement: Time Between Submission and Response: "+(responseTime-submissionTime));
            if(transactionReceipt.getStatus().equals("0")){
                transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PS_SettlingPayments,
                        round, Constants.TransactionStatus.FAILED,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
            }
            else {
                transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PS_SettlingPayments,
                        round, Constants.TransactionStatus.SUCCESS,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
            }
            Report.pushTransactionReport(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Payment Settlement Finished for round " +round);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetPoolMarket(int round) {
        System.out.println("DSO: Resetting Pool Market");
        try {
            TransactionReceipt transactionReceipt= poolMarketContract.SendDataAndResetPoolMarket().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DSO:  Pool Market resetting Finished for Round "+round);


        try {
            System.out.println("DSO: Number of Consumer Winners from PS " +round+ ":"+paymentSettlementContract.getPoolMarketConsumerWinners().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Number of Supplier Winners from PS  " +round+ ":"+paymentSettlementContract.getPoolMarketSupplierWinners().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Number of Consumer Dispatches from PS " +round+ ":"+paymentSettlementContract.getPoolMarketConsumerEnergyDispatch().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Number of Supplier Dispatches from PS  " +round+ ":"+paymentSettlementContract.getPoolMarketSupplierEnergyDispatch().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeBidding(int round) {
        System.out.println("DSO: Initializing Bidding for round "+round); //Just change this to changing status
       // BigInteger biddingStartTime= BigInteger.valueOf(testSchedule.testRounds.get(round).biddingStartTime);
      //  BigInteger biddingEndTime= BigInteger.valueOf(testSchedule.testRounds.get(round).biddingEndTime);
        try {
            TransactionReceipt transactionReceipt= poolMarketContract.initializeBidding().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DSO:  Bidding initialized for round "+round);
    }

    private void clearMarket(int round)  {
        System.out.println("DSO: Starting Market Clearance for round "+round);
        TransactionReceipt transactionReceipt = null;
        try {
            submissionTime=System.currentTimeMillis();
            System.out.println("DOS: Market Clearance Submission Time: "+submissionTime);
            transactionReceipt= poolMarketContract.calculateMarkerClearingPrice().send();
            responseTime=System.currentTimeMillis();
            System.err.println("DSO: Market Clearance: Time Between Submission and Response: "+(responseTime-submissionTime));
            if(transactionReceipt.getStatus().equals("0")){
                transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PM_MarketClearance,
                        round, Constants.TransactionStatus.FAILED,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
            }
            else {
                transaction =new Transaction(transactionReceipt.getTransactionHash(),Constants.TransactionType.PM_MarketClearance,
                        round, Constants.TransactionStatus.SUCCESS,submissionTime,responseTime,transactionReceipt.getBlockNumber(),transactionReceipt.getGasUsed());
            }
            Report.pushTransactionReport(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Market Clearing Price for round " +round+ ":"+poolMarketContract.getMarketClearingPrice().send().doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Total Number of Consumers From PM " +round+ ":"+poolMarketContract.getConsumers().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Total Number of Suppliers From PM " +round+ ":"+poolMarketContract.getSuppliers().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Number of Consumer Winners from PM " +round+ ":"+poolMarketContract.getConsumerWinners().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DSO: Number of Supplier Winners from PM  " +round+ ":"+poolMarketContract.getSupplierWinners().send().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void listenForMarketClearance() {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, MainJavaClass.POOL_MARKET_CONTRACT_ADDRESS);
        poolMarketContract
                .marketClearedEventFlowable(filter)
                .subscribe(event -> {
                    System.out.println("Market Cleared Event Received");
                    if(event!=null) {
                        System.out.println("Market Clearing Price " + event.price);
                        marketClearingPrice = event.price;
                    }
                    synchronized (marketCleared){
                        marketCleared.notifyAll();
                    }
                });
    }

    private ArrayList<Task> getDSORoundTasks(int round) {
        ArrayList<Task> roundTasks;
        ArrayList<Task> DSORoundTasks=new ArrayList<>();
        roundTasks=testSchedule.testRounds.get(round).tasks;
        for(Task ts:roundTasks){
            if(ts.taskType==Constants.TransactionType.PM_Initialization || ts.taskType==Constants.TransactionType.PM_MarketClearance || ts.taskType==Constants.TransactionType.PM_SendingDataAndMarketResetting || ts.taskType==Constants.TransactionType.PS_SettlingPayments || ts.taskType==Constants.TransactionType.PS_Resetting){
                DSORoundTasks.add(ts);
                System.out.println("DSO Round : "+round+" Tasks " + ts.taskType);
            }
        }
        return DSORoundTasks;
    }

}
