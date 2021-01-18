import SolidityJava.EnergyCoin;
import SolidityJava.PaymentSettlement;
import SolidityJava.PoolMarket;
import okhttp3.OkHttpClient;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.reactivestreams.Subscription;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.math.BigInteger.valueOf;

public class MainJavaClass {
    public static String POOL_MARKET_CONTRACT_ADDRESS;
    public static String EnergyCoin_CONTRACT_ADDRESS;
    public static String PaymentSettlement_CONTRACT_ADDRESS;
    public static ArrayList<WalletFile> walletFiles = new ArrayList<>();
    public static ArrayList<Thread> smartMeters = new ArrayList<>();
    public static ArrayList<String> urls = new ArrayList<>();
    static ArrayList<String> allPrivateKeys = null;
    static List<String> allAddresses = null;
    static int numberOfUserAccounts = 20000; // The number of accounts should not be less than the number of transactions in each round as each transaction uses one account.
    static int startingPort = 8545; //Make sure that the port is the same as the one in the Blockchain Network
    static int totalNumberOfNodes = 20; //Make sure that this is not more than the number of nodes in the Blockchain Network
    static int numberOfTrustedNodes = 1; // We have only one trusted node which is the DSO
    static ArrayList<EnergyData> totalEnergyDataList = new ArrayList<>();
    static ArrayList<TestRound> testRounds = new ArrayList<>();
    static int startUpDelay = 180000; //This is in milliseconds equal to 30 seconds
    static int finishingDelay = 600000; //This is in milliseconds equal to 30 seconds
    static int shortIntervalDelay = 60000; //This is in milliseconds equal to 30 seconds
    static int mediumIntervalDelay = 120000; //This is in milliseconds equal to 30 seconds
    static int longIntervalDelay = 180000; //This is in milliseconds equal to 2 minute
    static int initialRate = 1000; //Initial number Of transactions for the task durations (IT should be divisible by task durations without reminder. Currently the task duration is 60 seconds )
    static Constants.RateType rateType = Constants.RateType.INCREMENTAL;
    static long rateIncrement = 1000; //Should be set to zero for fixed rate
    static int numberOfRounds = 10;
    static Constants.ConsensusAlgorithm consensusAlgorithm = Constants.ConsensusAlgorithm.Proof_Of_Authority_IBFT;
    private static TestSchedule testSchedule;
    static EthBlock.Block firstBlock;
    static EthBlock.Block lastBlock;
    private static HashMap<BigInteger, CustomBlock> blockList = new HashMap<>();
    private static Subscription subscription;
    private static ArrayList<String> consumerAddresses;
    private static ArrayList<String> producerAddresses;
    private static ArrayList<String> filteredConsumerAddresses = new ArrayList<>();
    private static ArrayList<String> filteredProducerAddresses= new ArrayList<>();
    private static String DSOAddress;
    private static String DSOPrivateKey;

    private static ArrayList<String> consumerPrivateKeys;
    private static ArrayList<String> producerPrivateKeys;
    private static ArrayList<String> filteredConsumerPrivateKeys= new ArrayList<>();
    private static ArrayList<String> filteredProducerPrivateKeys= new ArrayList<>();
    private static ArrayList<ConsumerRegister> consumerRegisters= new ArrayList<>();
    private static ArrayList<ProducerRegister> producerRegisters=new ArrayList<>();
    private static ArrayList<Integer> consumerRegisteredFlag=new ArrayList<>();
    private static ArrayList<Integer> producerRegisteredFlag=new ArrayList<>();
    public static Web3j DSONode;
    static OkHttpClient okHttpClient;

    public static void main(String[] args) {

        //Creating web3j HTTP urls pointing to the blockchain nodes
        // The total number of urls is equal to the numberOfSmartMeters +numberofTrustedNodes (The trusted node is DSO)
        //DSO uses the first url
        int port = 0;
        for (int i = 0; i < totalNumberOfNodes; i++) {
            port = startingPort + i;
           String url= "http://localhost:"+String.valueOf(port);
           // Web3j node=Web3j.build(new HttpService("http://localhost:"+String.valueOf(port)),1000, Async.defaultExecutorService() );
            urls.add(url);
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(10, TimeUnit.MINUTES) // write timeout
                .readTimeout(10, TimeUnit.MINUTES); // read timeout
        okHttpClient = builder.build();

        DSONode=Web3j.build(new HttpService(urls.get(0), okHttpClient,false) );


        //Creating accounts and writing to file. This is done only once the first time. This is done only once
        //Write the private keys to a separate file because we use the private keys to create credentials for smart meters
        //  AccountManager.createAccounts(150000);
        //  AccountManager.writeAccountsToFile();
        //  System.out.println("Account Generation Finished");
        //  AccountManager.writePrivateKeysToFile();
        //   AccountManager.writeAddressesToFile();
        // System.out.println("Private Keys and Addresses Written to file");

        //Import Private Keys and addresses
        allPrivateKeys = AccountManager.getPrivateKeys(numberOfUserAccounts);
        allAddresses = AccountManager.getAddresses(numberOfUserAccounts);
        System.out.println("Total number of Private Keys: " + allPrivateKeys.size());
        System.out.println("Total Number of Addresses: " + allAddresses.size());

        DSOAddress=allAddresses.get(0);
        DSOPrivateKey=allPrivateKeys.get(0);
        consumerAddresses = new ArrayList<String>(allAddresses.subList(1, allAddresses.size() / 2)); // The first address is assigned to DSO
        producerAddresses = new ArrayList<String>(allAddresses.subList(allAddresses.size()/2 +1, allAddresses.size()));
        consumerPrivateKeys= new ArrayList<String>(allPrivateKeys.subList(1, allPrivateKeys.size() / 2));
        producerPrivateKeys= new ArrayList<String>(allPrivateKeys.subList(allPrivateKeys.size()/2 +1, allPrivateKeys.size()));

        System.out.println("Total Number of Consumer Addresses: " + consumerAddresses.size());
        System.out.println("Total Number of Producer Addresses: " + producerAddresses.size());


        // Create the contracts. //The Smart Contracts are created by using DSO's Credentials. The first node is used for DSO
        deployContracts(DSONode, AccountManager.createCredentials(DSOPrivateKey));


        registerAllUsersOnBlockchain();
        //Since some registartion could fail, retrieve which users are properly registered
      getRegisteredConsumers(DSONode, AccountManager.createCredentials(DSOPrivateKey));
      getRegisteredProducers(DSONode, AccountManager.createCredentials(DSOPrivateKey));

        System.out.println("Number of Registered Consumer Addresses: " + filteredConsumerAddresses.size());
        System.out.println("Number of Registered Producer Addresses: " + filteredProducerAddresses.size());


        long testStartTime= System.currentTimeMillis() + startUpDelay; // Sometime in the future
        System.out.println("Test Start Time: "+testStartTime);
        long roundDuration=0;

        //Create rate controller for the test
        RateController rateController = new RateController(rateType,rateIncrement,initialRate); //Increment should be set to zero for Fixed rate

        //Create rounds and list of tasks for each of teh rounds
        // Set different parameters for each of the tasks. The parameters apply to one round. Add the tasks to round
        // We can set different transaction rate for different rounds
       for (int i =0; i <numberOfRounds;i++){
            // long roundStartTime=0;
            TestRound testRound= new TestRound(i);
           //Add the test round to the list of test rounds
           testRounds.add(testRound);
            //Create round tasks for each of the rounds.
            // The tasks should be put in the correct sequential order. Start with BiM tasks, then PM tasks, then BAM tasks and finally PS tasks
            testRound.tasks = addRoundTasks(); // We can have different types of tasks for different rounds but for now, we are using the same list of task for all rounds

            // Determine the round duration based on the selected tasks
            roundDuration=0; //we assume the same duration for all rounds as we have the same list of tasks but the round duration can be set different for different rounds
            for(Task task:testRound.tasks){ //Assuming that the tasks are put in the correct sequential order
                if(task.taskType==Constants.TransactionType.BiM_MultiSigContract || task.taskType==Constants.TransactionType.PM_Bidding ||task.taskType==Constants.TransactionType.BaM_Offers ){
                    roundDuration+=task.duration+longIntervalDelay;
                }
                else if(task.taskType==Constants.TransactionType.PM_getDispachedEnergy ||task.taskType==Constants.TransactionType.PS_getBalance){
                    roundDuration+=task.duration+mediumIntervalDelay;
                }
                else{
                    roundDuration+=task.duration+shortIntervalDelay;
                }
            }
           testRound.roundDuration=roundDuration;

           //Determine round start. Again we assume the same duration for all rounds as we have the same list of tasks for all rounds
            if (i==0) {// if this is the first round
                testRound.roundStartTime=testStartTime +mediumIntervalDelay; // To give more time for the first round
                testRound.roundEndTime=testRound.roundStartTime+ testRound.roundDuration;
            }
            else {   // The start of the next round is the same us the end of the previous round. There is no time gap between the two.
                testRound.roundStartTime = testRounds.get(i-1).roundEndTime +mediumIntervalDelay;// We also add short delay between rounds not inside a round
                testRound.roundEndTime=testRound.roundStartTime+testRound.roundDuration;
            }

           //Determine the start and end time for each of the tasks in the round
            for(int j=0;j<testRound.tasks.size();j++) { //We assume that the tasks are put in the correct sequential order
                Task task = testRound.tasks.get(j);
                if (j == 0) { //The first task in the round is market initialization
                    task.startTime = testRound.roundStartTime + shortIntervalDelay; //Because tThe first task is usually Market initialization sent by the dSO, so shwort interval is enough
                } else {
                    Task previousTask = testRound.tasks.get(j - 1); //The starting time of the current task is determined based on the type of task prior to this task
                    if (previousTask.taskType == Constants.TransactionType.BiM_MultiSigContract || previousTask.taskType == Constants.TransactionType.PM_Bidding || previousTask.taskType == Constants.TransactionType.BaM_Offers) {
                        // We use long duration after each of the above tasks
                        task.startTime = previousTask.endTime + longIntervalDelay;
                    }
                    else if(previousTask.taskType==Constants.TransactionType.PM_getDispachedEnergy || previousTask.taskType==Constants.TransactionType.PS_getBalance){
                        task.startTime = previousTask.endTime +mediumIntervalDelay;
                    }
                    else {
                        task.startTime = previousTask.endTime + shortIntervalDelay;
                    }
                }
                task.endTime = task.startTime + task.duration;
            }

           // Set the transaction rate and
           //The number of transactions in each round should not be greater than the number of accounts registered on the Blockchain as each transaction uses different account
           for(Task task:testRound.tasks){
              task.totalNumberOfTransactionsPerRound=rateController.getNumberOfTransactionsForThisRound(i,task.taskType);
              task.systemWideTransactionRate=task.totalNumberOfTransactionsPerRound/(task.duration/1000);

              if(task.taskType==Constants.TransactionType.BiM_MultiSigContract || task.taskType==Constants.TransactionType.PM_Bidding || task.taskType==Constants.TransactionType.BaM_Offers || task.taskType==Constants.TransactionType.PM_getDispachedEnergy || task.taskType==Constants.TransactionType.PS_getBalance){
                task.transactionRatePerClient=task.systemWideTransactionRate/2; // There are two groups of clients i.e. consumer and producer client. And each of them have several worker threads
                 // totalNumberOfTransactionsPerRound and systemWideTransactionRate should be divisible by number of clients without reminder as it uses long not double. or Different transaction rate should be assigned for consumer and producer separately
                  task.numberOfTransactionsPerClient=task.totalNumberOfTransactionsPerRound/2; // Currently, the number of transactions sent by the consumer and producers is equal but could be modified to make it different
               }
               else{
                   // All other types of transactions are sent by DSO and they are just one transaction per round
                   task.transactionRatePerClient=1;
                   task.numberOfTransactionsPerClient=1;
               }
           }


           System.out.println("Round " +i+ " Start Time "+testRound.roundStartTime);
           for(int j=0;j<testRound.tasks.size();j++){
               System.out.println("Round " +i+ " Task "+j+" Type: "+testRound.tasks.get(j).taskType+" Start Time: "+testRound.tasks.get(j).startTime+" End Time: "+testRound.tasks.get(j).endTime+" Duration: "+testRound.tasks.get(j).duration+
                       " System Wide Transaction Rate: "+testRound.tasks.get(j).systemWideTransactionRate+" Total Number of Transactions: "+testRound.tasks.get(j).totalNumberOfTransactionsPerRound);

           }
           System.out.println("Round " +i+ " End Time "+testRound.roundEndTime);
           System.out.println("Round " +i+ " Round Duration "+testRound.roundDuration);
        }

        long testDuration= numberOfRounds*(roundDuration+ mediumIntervalDelay);// We assume that all rounds have the same duration. This does not work if the rounds have different duration
        long testEndTime= testStartTime+testDuration +finishingDelay; //We add finishing delay to give time for any unfinished task
        System.out.println("Test End Time: "+testEndTime);
        System.out.println("Test Duration: "+testDuration+finishingDelay);

        //Create test schedule consisting of all rounds above
        testSchedule= new TestSchedule(testStartTime,testEndTime,numberOfRounds,testRounds);
        Report.setTestSchedule(testSchedule);


        // create the DSO
        Thread dso= new Thread (new DSO(testSchedule,DSONode, DSOPrivateKey),"DSO"); //The first private key is assigned to DSO


       //Create the smart meters

        Thread consumer= new Thread(new SmartMeter(Constants.Role.CONSUMER,urls, filteredConsumerPrivateKeys, testSchedule),"Consumer");
                smartMeters.add(consumer);
         Thread producer= new Thread(new SmartMeter(Constants.Role.PRODUCER, urls, filteredProducerPrivateKeys, testSchedule),"Producer");
                smartMeters.add(producer);


        firstBlock=getTheCurrentBlock();

        //Start Dso
      dso.start();

        //Start the smart meters
        consumer.start();
        producer.start();

      try {
           Thread.sleep((testSchedule.endTime-System.currentTimeMillis()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      lastBlock =getTheCurrentBlock();
      long time=System.currentTimeMillis();
         getAllBlocksStartingFrom(firstBlock, lastBlock);
        System.out.println(" Getting Blocks Duration: "+(System.currentTimeMillis()-time));
        try {
            Thread.sleep(finishingDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

     subscription.cancel();

    Report.findBlockNumberOfTransactions(blockList);
    Report.calculateConfirmationTime(consensusAlgorithm);
    Report.groupTransactionByType();
    Report.calculateTheNumberOfSuccessfulAndFailedTransactions();
    Report.calculateRoundTransactionLatency();
    Report.calculateRoundThroughtput();
    Report.calculateGasUsed();
    Report.calculateSummaryResult();
    Report.printReport();
    Report.writeToFile();

    System.out.println("==========================End of the Test===============================");

    }


    private static void registerAllUsersOnBlockchain() {

        long registerStartTime = System.currentTimeMillis() + 5000;
        System.out.println("Register Start Time: " + registerStartTime);
        int partition = 150;
        int numberOfTimers = 0;

        for (int i = 0; i <= consumerAddresses.size(); i += partition) {
            Timer timer = new Timer();
            ConsumerRegister consumerRegister = new ConsumerRegister(DSONode, AccountManager.createCredentials(DSOPrivateKey), i, partition);
            consumerRegisters.add(consumerRegister);
            Date date = new Date(registerStartTime + numberOfTimers * 100); //Each 10 milliseconds
            timer.schedule(consumerRegister, date);
            numberOfTimers++;
        }


        partition = 75;
        for (int i = 0; i <= producerAddresses.size(); i += partition) {
            Timer timer = new Timer();
            ProducerRegister producerRegister = new ProducerRegister(DSONode, AccountManager.createCredentials(DSOPrivateKey), i, partition);
            producerRegisters.add(producerRegister);
            Date date = new Date(registerStartTime + numberOfTimers * 100); //Each 10 milliseconds
            timer.schedule(producerRegister, date);
            numberOfTimers++;
        }

        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All users Registered: "+(registerStartTime + numberOfTimers*100));
    }

    private static EthBlock.Block getTheCurrentBlock() {
        EthBlock.Block block = null;
        try {
            block = DSONode.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
            System.out.println("Current Block Number: "+block.getNumber());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return block;
    }

    public static void getAllBlocksStartingFrom(EthBlock.Block firstBlock, EthBlock.Block lastBlock){
       subscription = (Subscription) DSONode.replayPastBlocksFlowable(
               new DefaultBlockParameterNumber(firstBlock.getNumber()),    new DefaultBlockParameterNumber(lastBlock.getNumber()), true).subscribe(block -> {
           CustomBlock customBlock = new CustomBlock(block.getBlock(), System.currentTimeMillis());
            blockList.put(customBlock.block.getNumber(), customBlock);
           // System.err.println("Block Number: "+block.getBlock().getNumber());
        });
    }

    // The tasks should be put in the correct sequential order. Start with BiM tasks, then PM tasks, then BAM tasks and finally PS tasks
    //Also set the contractID, methodToInvoke and data during Task object creation
    // It is better to run either only write transactions such as bidding and market clearance or only read transaction such as get balance.
    // Otherwise, there will be high congestion and failure and the result will be bad

    private static ArrayList<Task> addRoundTasks() { //Also set the contractID, methodToInvoke and data during Task object creation
        Task task;
        ArrayList<Task> roundTasks= new ArrayList<>();
  /*    task= new Task(Constants.TransactionType.BiM_MultiSigContract, TaskDurations.BiM_MultiSigContractDuration);
        allPossibleTasks.add(task);
        task= new Task(Constants.TransactionType.BiM_Resetting, TaskDurations.BiM_ResettingDuration);
        allPossibleTasks.add(task);*/
        task= new Task(Constants.TransactionType.PM_Initialization,TaskDurations.PM_InitializationDuration);
        roundTasks.add(task);
        task= new Task(Constants.TransactionType.PM_Bidding,TaskDurations.PM_BiddingDuration);
        roundTasks.add(task);
        task= new Task(Constants.TransactionType.PM_MarketClearance,TaskDurations.PM_MarketClearanceDuration);
        roundTasks.add(task);
       // task= new Task(Constants.TransactionType.PM_getDispachedEnergy,TaskDurations.PM_getDispachedEnergyDuration);
       // roundTasks.add(task);
        task= new Task(Constants.TransactionType.PM_SendingDataAndMarketResetting,TaskDurations.PM_MarketResettingDuration);
        roundTasks.add(task);
/*        task= new Task(Constants.TransactionType.BaM_CalculateMismatch,TaskDurations.BaM_CalculateMismatchDuration);
        allPossibleTasks.add(task);
        task= new Task(Constants.TransactionType.BaM_Offers,TaskDurations.BaM_OffersDuration);
        allPossibleTasks.add(task);
        task= new Task(Constants.TransactionType.BaM_MarketClearance,TaskDurations.BaM_MarketClearanceDuration);
        allPossibleTasks.add(task);
        task= new Task(Constants.TransactionType.BaM_MarketResetting,TaskDurations.BaM_MarketResettingDuration);
        allPossibleTasks.add(task);
        task= new Task(Constants.TransactionType.PS_ConsumptionData,TaskDurations.PS_ConsumptionDuration);
        allPossibleTasks.add(task);
        task= new Task(Constants.TransactionType.PS_ProductionData,TaskDurations.PS_ProductionDuration);
        allPossibleTasks.add(task); */
        task= new Task(Constants.TransactionType.PS_SettlingPayments,TaskDurations.PS_SettlePaymentsDuration);
        roundTasks.add(task);
       // task= new Task(Constants.TransactionType.PS_getBalance,TaskDurations.PS_getBalanceDuration);
      //  roundTasks.add(task);
        task= new Task(Constants.TransactionType.PS_Resetting,TaskDurations.PS_ResettingDuration);
        roundTasks.add(task);
       return roundTasks;
    }

    private static void readPowerData(int round, Task task) throws IOException, InvalidFormatException {
        // File path to energy data
        String powerDataFile = "C:/Users/s3753266/RMIT-C/UBETA-Ethereum/PowerData.xlsx"; // This is only for PM_Bidding
        Workbook workbook = WorkbookFactory.create(new File(powerDataFile));
        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);

      //The row number is equal to the number of total transactions per client for one type of task
         for (Row row: sheet) {
             EnergyData energyData = new EnergyData();
             energyData.smartMeterID=row.getRowNum();//The row number is equal to the number of total transactions per client for one type of task
             energyData.roundNo=round;
             energyData.taskType=task.taskType;
             BigInteger [] energyPricePair= new BigInteger [2];
             for (Cell cell : row) {
                if (cell.getColumnIndex() == 0) {
                    energyPricePair[0] = BigInteger.valueOf((long) cell.getNumericCellValue()); //Energy Amount
                } else  if(cell.getColumnIndex() == 1){
                    energyPricePair[1] = BigInteger.valueOf((long) cell.getNumericCellValue()); //Energy Price
                }
               //  columnNumber++;
            }
             for(int i=0; i<task.numberOfTransactionsPerClient;i++){ //i here represents the ith data argument for the ith transaction to be sent by a smart meter client.
              energyData.energyPricePairList.add(energyPricePair); //Since we only have 10 bid data for now, a smart meter client is re-using the same data for all transactions to be sent by that specific smart meter for this specific task in this round
             }
             System.out.println("Reading Power data for round "+round+ "Task Type: "+task.taskType);
             totalEnergyDataList.add(energyData);
            // System.out.println("Energy Bid: "+energyPricePair[0]+", "+energyPricePair[1]);
        }
    }


    // this method has to be called only once when the contract is deployed. The call is as follows

    private static void deployContracts(Web3j web3j, Credentials credentials) {
       // Deploy Energy Coin Contract
        String  coinName ="EnergyCoin";
        String coinSymbol ="EC";
        BigInteger decimals =valueOf(10);
        BigInteger totalSupply=valueOf(1_000_000_000L);
        BigInteger exchangeRate =valueOf(10);

        try {
            EnergyCoin_CONTRACT_ADDRESS= EnergyCoin.deploy(web3j, credentials, new DefaultGasProvider(),coinName,coinSymbol,decimals,totalSupply,exchangeRate).send().getContractAddress();
            System.out.println("Energy Coin CONTRACT_ADDRESS: " +EnergyCoin_CONTRACT_ADDRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Deploy Payment Settlement Contract

        try {
            PaymentSettlement_CONTRACT_ADDRESS= PaymentSettlement.deploy(web3j, credentials, new DefaultGasProvider(), EnergyCoin_CONTRACT_ADDRESS)
                    .send()
                    .getContractAddress();
            System.out.println("Payment Settlement CONTRACT_ADDRESS: " +PaymentSettlement_CONTRACT_ADDRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Deploy Pool Market Contract
        try {
            POOL_MARKET_CONTRACT_ADDRESS= PoolMarket.deploy(web3j, credentials, new DefaultGasProvider(), PaymentSettlement_CONTRACT_ADDRESS, EnergyCoin_CONTRACT_ADDRESS)
                    .send()
                    .getContractAddress();
            System.out.println("Pool Market CONTRACT_ADDRESS: " +POOL_MARKET_CONTRACT_ADDRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void getRegisteredConsumers(Web3j web3j, Credentials credentials){
        try {
             EnergyCoin energyCoinContract = EnergyCoin.load(MainJavaClass.EnergyCoin_CONTRACT_ADDRESS, web3j, credentials, new DefaultGasProvider());
            for (int i=0; i<consumerAddresses.size();i++) {
              if(energyCoinContract.getBalance(consumerAddresses.get(i)).send().doubleValue()>0){
                  filteredConsumerAddresses.add(consumerAddresses.get(i));
                  filteredConsumerPrivateKeys.add(consumerPrivateKeys.get(i));
              }
            }
            } catch(Exception e){
                e.printStackTrace();
            }
    }

    public static void getRegisteredProducers(Web3j web3j, Credentials credentials){
        try {
            EnergyCoin energyCoinContract = EnergyCoin.load(MainJavaClass.EnergyCoin_CONTRACT_ADDRESS, web3j, credentials, new DefaultGasProvider());
            for (int i = 0; i < producerAddresses.size(); i++) {
                if (energyCoinContract.getBalance(producerAddresses.get(i)).send().doubleValue() > 0) {
                        filteredProducerAddresses.add(producerAddresses.get(i));
                        filteredProducerPrivateKeys.add(producerPrivateKeys.get(i));
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    protected static class ConsumerRegister extends TimerTask {
        Credentials credentials;
        Web3j web3j;
        List<String> addresses;
        BigInteger initialBalance =valueOf(1000000000);
        int index;
        int partitionSize;
        public ConsumerRegister (Web3j web3j, Credentials credentials, int index, int partitionSize){
            this.web3j=web3j;
            this.credentials = credentials;
            this.index=index;
            this.partitionSize=partitionSize;

        }
        @Override
        public void run() {
            if((index+partitionSize) <=consumerAddresses.size()){
                this.addresses=MainJavaClass.consumerAddresses.subList(index, index+partitionSize); // The last element is excluded
                System.out.println("Registering Consumers from: "+index +" to "+(index + partitionSize-1));
            }
            else {
                this.addresses=MainJavaClass.consumerAddresses.subList(index, consumerAddresses.size());
                System.out.println("Registering Consumers from: "+index +" to "+(consumerAddresses.size()-1));
            }

            EnergyCoin energyCoinContract = EnergyCoin.load(MainJavaClass.EnergyCoin_CONTRACT_ADDRESS, web3j, credentials, new DefaultGasProvider());
            try {
                energyCoinContract.registerConsumers(addresses, initialBalance).send();
                MainJavaClass.notifyConsumerRegistry(this.index);
            } catch (Exception e) {
               // System.err.println(" Consumers Cannot Be Registered");
            }

        }
    }




    protected static class ProducerRegister extends TimerTask {
        Credentials credentials;
        Web3j web3j;
        List<String> addresses;
        BigInteger initialBalance =valueOf(1000000000);
        BigInteger productionCapacity=valueOf(1000000000);
        int index;
        int partitionSize;
        public ProducerRegister (Web3j web3j, Credentials credentials, int index, int partitionSize){
            this.web3j=web3j;
            this.credentials = credentials;
            this.credentials = credentials;
            this.index=index;
            this.partitionSize= partitionSize;

        }
        @Override
        public void run() {
            if((index+partitionSize) <=producerAddresses.size()){
                this.addresses=MainJavaClass.producerAddresses.subList(index, (index+partitionSize));
                System.out.println("Registering Producers from: "+index +" to "+(index+partitionSize-1));
            }
            else {
                this.addresses=MainJavaClass.producerAddresses.subList(index, producerAddresses.size());
                System.out.println("Registering Producers from: "+index +" to "+(producerAddresses.size()-1));
            }

            EnergyCoin energyCoinContract = EnergyCoin.load(MainJavaClass.EnergyCoin_CONTRACT_ADDRESS, web3j, credentials, new DefaultGasProvider());

            try {
            energyCoinContract.registerProducers(addresses, initialBalance, productionCapacity).send();
                MainJavaClass.notifyProducerRegistry(this.index);
            } catch (Exception e) {
              //  System.err.println(" Producers Cannot Be Registered");
            }


        }
    }


    public static synchronized void notifyConsumerRegistry(Integer consumerRegister){
        consumerRegisteredFlag.add(consumerRegister);
    }

    public static synchronized void notifyProducerRegistry(Integer producerRegister){
        producerRegisteredFlag.add(producerRegister);
    }

   public static boolean allUsersAreNotRegistered(){
        boolean allUsersAreNotRegistered=false;
       for (int i = 0; i < consumerRegisters.size(); i++) {
           if (!consumerRegisteredFlag.contains(consumerRegisters.get(i).index)) {
               allUsersAreNotRegistered=true;
             break;
           }
       }

       for (int i = 0; i < producerRegisters.size(); i++) {
           if (!producerRegisteredFlag.contains(producerRegisters.get(i).index)) {
               allUsersAreNotRegistered=true;
               break;
           }
       }

        return allUsersAreNotRegistered;
   }



}






