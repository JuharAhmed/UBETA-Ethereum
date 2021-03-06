import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

// This class prints the report for the experiment
public class Report {
    static CustomBlock customBlock;
    static Transaction transaction;
    static ArrayList<Transaction> listOfALLTransactions= new ArrayList<>();
    static ArrayList<QueryTransaction> listOfALLQueryTransactions= new ArrayList<>();
    static ArrayList<Transaction> biddingTransactionList = new ArrayList<>();
    static ArrayList<Transaction> marketClearanceTransactionList = new ArrayList<>();
    static ArrayList<Transaction> paymentSettlementTransactionList = new ArrayList<>();
    static ArrayList<QueryTransaction> getEnergyDispatchTransactionList = new ArrayList<>();
    static ArrayList<QueryTransaction> getBalanceTransactionList = new ArrayList<>();

    static HashMap<BigInteger, CustomBlock> blockListFromMonitor = new HashMap<>();
    static HashMap<BigInteger, CustomBlock> blockListFromMain= new HashMap<>();
    static TestSchedule testSchedule;
    static BigInteger confirmationBlockNumber;
    static double confimationTime;
    static ArrayList<RoundResult> biddingRoundResults= new ArrayList<>();
    static ArrayList<RoundResult> marketClearanceRoundResults= new ArrayList<>();
    static ArrayList<RoundResult> paymentSettlementRoundResults= new ArrayList<>();
    static ArrayList<RoundResult> getDispatchedEnergyRoundResults= new ArrayList<>();
    static ArrayList<RoundResult> getBalanceRoundResults= new ArrayList<>();

    static SummaryResult biddingSummaryResult = new SummaryResult(Constants.TransactionType.PM_Bidding);
    static SummaryResult marketClearanceSummaryResult= new SummaryResult(Constants.TransactionType.PM_MarketClearance);
    static SummaryResult paymentSettlementSummaryResult= new SummaryResult(Constants.TransactionType.PS_SettlingPayments);
    static SummaryResult getDispatchedEnergySummaryResult= new SummaryResult(Constants.TransactionType.PM_getDispachedEnergy);
    static SummaryResult getBalanceSummaryResult= new SummaryResult(Constants.TransactionType.PS_getBalance);

    public static void setTestSchedule(TestSchedule testSchedule) {
        Report.testSchedule = testSchedule;
    }

    // This method finds the block number for each trasaction as we are using the block's timestamp for confirmation time
    public static void findBlockNumberOfTransactions(HashMap<BigInteger,CustomBlock> blockList){
        blockListFromMain=blockList;
        for(CustomBlock customBlock: blockList.values()){
            for(EthBlock.TransactionResult transactionResult: customBlock.block.getTransactions()){
                EthBlock.TransactionObject transactionObject= (EthBlock.TransactionObject)transactionResult.get();
                for(Transaction tr: listOfALLTransactions) {
                    if (tr.Status == Constants.TransactionStatus.SUCCESS){
                        if (tr.ID.equals(transactionObject.getHash())) {
                             if((customBlock.block.getNumber().compareTo(tr.blockNumber))==1){
                                 System.err.println(" Block Number Different Initial Block Number: "+tr.blockNumber+" Updated Block Number: "+customBlock.block.getNumber());
                               tr.blockNumber = customBlock.block.getNumber();
                             }
                    }
                }
                }
            }
        }
    }

// Calculate the confirmation for each transaction
    public static void calculateConfirmationTime(Constants.ConsensusAlgorithm consensusAlgorithm){
        // Calculate Comitted Time
        int numberOfBlocksMissed=0;
    for(Transaction tr: listOfALLTransactions){
        if((tr.Status==Constants.TransactionStatus.SUCCESS) && (consensusAlgorithm==Constants.ConsensusAlgorithm.Proof_Of_Authority_IBFT)){ //We assume that the transaction is comitted after 2 blocks for PoA
           confirmationBlockNumber=tr.blockNumber.add(BigInteger.valueOf(1));
            // confimationTime=blockListFromMonitor.get(confirmationBlockNumber).timeReceived;
            if(blockListFromMain.containsKey(confirmationBlockNumber)){
                confimationTime=blockListFromMain.get(confirmationBlockNumber).block.getTimestamp().doubleValue()*1000;
            }
            else { // Apply this the blockc for the transaction ws not received during block listening
                long time1=System.currentTimeMillis();
                confimationTime=getBlockByNumber(confirmationBlockNumber).getTimestamp().doubleValue()*1000;
                System.out.println(" Report get Block Duration: "+(System.currentTimeMillis()-time1));
                numberOfBlocksMissed++;
            }
            tr.confimationTime=confimationTime;

/*          System.out.println("Report: Round : "+ tr.round);
            System.out.println("Report: Type of Transaction : "+tr.Type);
            System.out.println("Report: Block Number of the Transaction: "+tr.blockNumber);
            System.out.println("Report: Submission Time: "+tr.submissionTime);
            System.out.println("Report: Confirmation Block Number: "+confirmationBlockNumber);
            System.out.println("Report: Confirmation TimeStamp: "+confimationTime);
            System.out.println("Report: Transaction Latency : "+(confimationTime-tr.submissionTime));
            System.out.println("Report: Gas Used : "+tr.gasUSed);*/
        }
        else if((tr.Status==Constants.TransactionStatus.SUCCESS) && (consensusAlgorithm==Constants.ConsensusAlgorithm.Proof_Of_Work)){ //We assume that the transaction is comitted after 6 blocks for PoA
            confirmationBlockNumber=tr.blockNumber.add(BigInteger.valueOf(6));
            if(blockListFromMain.containsKey(confirmationBlockNumber)){
                confimationTime=blockListFromMain.get(confirmationBlockNumber).block.getTimestamp().doubleValue()*1000;
            }
            else {
                confimationTime=getBlockByNumber(confirmationBlockNumber).getTimestamp().doubleValue()*1000;
            }
            tr.confimationTime=confimationTime;
          //  System.out.println("Report: Confirmation Time from Monitor: "+confimationTime);
          //  System.out.println("Report: Round : "+ tr.round);
         //   System.out.println("Report: Type of Transaction : "+tr.Type);
/*            System.out.println("Report: Submission Time: "+tr.submissionTime);
            System.out.println("Report: Block Number of the Transaction: "+tr.blockNumber);
            System.out.println("Report: Confirmation Block Number: "+confirmationBlockNumber);
            System.out.println("Report: Confirmation TimeStamp: "+confimationTime);
            System.out.println("Report: Transaction Latency : "+(confimationTime-tr.submissionTime));*/
        }
        else if((tr.Status==Constants.TransactionStatus.SUCCESS) && (consensusAlgorithm==Constants.ConsensusAlgorithm.Proof_Of_Authority_Clique)){ //We assume that the transaction is comitted after 6 blocks for PoA
            confirmationBlockNumber=tr.blockNumber.add(BigInteger.valueOf(6));
            // confimationTime=blockListFromMonitor.get(confirmationBlockNumber).timeReceived;
            if(blockListFromMain.containsKey(confirmationBlockNumber)){
                confimationTime=blockListFromMain.get(confirmationBlockNumber).block.getTimestamp().doubleValue()*1000;
            }
            else {
                confimationTime=getBlockByNumber(confirmationBlockNumber).getTimestamp().doubleValue()*1000;
            }
            tr.confimationTime=confimationTime;

   /*       System.out.println("Report: Round : "+ tr.round);
            System.out.println("Report: Type of Transaction : "+tr.Type);
            System.out.println("Report: Submission Time: "+tr.submissionTime);
            System.out.println("Report: Block Number of Transaction : "+tr.blockNumber);
            System.out.println("Report: Confirmation Block Number: "+confirmationBlockNumber);
            System.out.println("Report: Confirmation Timestamp: "+confimationTime);
            System.out.println("Report: Transaction Latency based on Time Stamp : "+(confimationTime-tr.submissionTime)+" Bsed on Received Time: "+((blockListFromMonitor.get(confirmationBlockNumber).timeReceived)-tr.submissionTime));*/
        }
    }
    System.out.println("Number of Blocks Missed: "+numberOfBlocksMissed);
    }

    private static EthBlock.Block getBlockByNumber(BigInteger confirmationBlockNumber) {
        EthBlock ethBlock = null;
        try {
            ethBlock = MainJavaClass.DSONode.ethGetBlockByNumber(new DefaultBlockParameterNumber(confirmationBlockNumber), true).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ethBlock.getBlock();
    }

    //Group Transactions by Type
    public static void groupTransactionByType() {

        System.err.println("Report: Total Number of Invoke transactions: "+listOfALLTransactions.size());

        System.err.println("Report: Total Number of get balance transactions: "+listOfALLQueryTransactions.size());

        for (Transaction tr : listOfALLTransactions) {
            if (tr.Type == Constants.TransactionType.PM_Bidding) {
                biddingTransactionList.add(tr);
            } else if (tr.Type == Constants.TransactionType.PM_MarketClearance) {
                marketClearanceTransactionList.add(tr);
            } else if (tr.Type == Constants.TransactionType.PS_SettlingPayments) {
                paymentSettlementTransactionList.add(tr);
            } else {
                //We can add more types
            }
        }

        for (QueryTransaction tr : listOfALLQueryTransactions) {
            if (tr.Type == Constants.TransactionType.PM_getDispachedEnergy) {
                getEnergyDispatchTransactionList.add(tr);
            } else if (tr.Type == Constants.TransactionType.PS_getBalance) {
                getBalanceTransactionList.add(tr);
            }
            else {
                //We can add more types
            }
        }

    }
    
    public static void calculateTheNumberOfSuccessfulAndFailedTransactions (){ // Calculate the number of succesful and failed transactions for each round

        int numberOfSuccessfulTransactions;
        int numberOfFailedTransactions;
        for(int r=0; r<testSchedule.numberOfRounds;r++){
            RoundResult biddingRoundResult= new RoundResult(r, Constants.TransactionType.PM_Bidding);
            RoundResult marketClearanceRoundResult= new RoundResult(r, Constants.TransactionType.PM_MarketClearance);
            RoundResult paymentSettlementRoundResult= new RoundResult(r, Constants.TransactionType.PS_SettlingPayments);
            RoundResult getDispatchedEnergyRoundResult = new RoundResult(r, Constants.TransactionType.PM_getDispachedEnergy);
            RoundResult getBalanceRoundResult = new RoundResult(r, Constants.TransactionType.PS_getBalance);
            numberOfSuccessfulTransactions=0;
            numberOfFailedTransactions=0;
            for(Transaction tr: biddingTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS) {
                    numberOfSuccessfulTransactions++;
                }
                else if(tr.round==r && tr.Status==Constants.TransactionStatus.FAILED) {
                    numberOfFailedTransactions++;
                }

            }
            biddingRoundResult.numberOfSuccessfulTransactions=numberOfSuccessfulTransactions;
            biddingRoundResult.numberOfFailedTransactions=numberOfFailedTransactions;
            biddingRoundResults.add(biddingRoundResult);


            numberOfSuccessfulTransactions=0;
            numberOfFailedTransactions=0;
            for(Transaction tr: marketClearanceTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS) {
                    numberOfSuccessfulTransactions++;
                }
                else if(tr.round==r && tr.Status==Constants.TransactionStatus.FAILED) {
                    numberOfFailedTransactions++;
                }

            }
            marketClearanceRoundResult.numberOfSuccessfulTransactions=numberOfSuccessfulTransactions;
            marketClearanceRoundResult.numberOfFailedTransactions=numberOfFailedTransactions;
            marketClearanceRoundResults.add(marketClearanceRoundResult);


            numberOfSuccessfulTransactions=0;
            numberOfFailedTransactions=0;
            for(Transaction tr: paymentSettlementTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS) {
                    numberOfSuccessfulTransactions++;
                }
                else if(tr.round==r && tr.Status==Constants.TransactionStatus.FAILED) {
                    numberOfFailedTransactions++;
                }

            }
            paymentSettlementRoundResult.numberOfSuccessfulTransactions=numberOfSuccessfulTransactions;
            paymentSettlementRoundResult.numberOfFailedTransactions=numberOfFailedTransactions;
            paymentSettlementRoundResults.add(paymentSettlementRoundResult);


            numberOfSuccessfulTransactions=0;
            numberOfFailedTransactions=0;
            for(QueryTransaction tr: getEnergyDispatchTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS) {
                    numberOfSuccessfulTransactions++;
                }
                else if(tr.round==r && tr.Status==Constants.TransactionStatus.FAILED) {
                    numberOfFailedTransactions++;
                }

            }
            getDispatchedEnergyRoundResult.numberOfSuccessfulTransactions=numberOfSuccessfulTransactions;
            getDispatchedEnergyRoundResult.numberOfFailedTransactions=numberOfFailedTransactions;
            getDispatchedEnergyRoundResults.add(getDispatchedEnergyRoundResult);


            numberOfSuccessfulTransactions=0;
            numberOfFailedTransactions=0;
            for(QueryTransaction tr: getBalanceTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS) {
                    numberOfSuccessfulTransactions++;
                }
                else if(tr.round==r && tr.Status==Constants.TransactionStatus.FAILED) {
                    numberOfFailedTransactions++;
                }

            }
            getBalanceRoundResult.numberOfSuccessfulTransactions=numberOfSuccessfulTransactions;
            getBalanceRoundResult.numberOfFailedTransactions=numberOfFailedTransactions;
            getBalanceRoundResults.add(getBalanceRoundResult);

        }
    }


    public static synchronized void  pushTransactionReport(Transaction transaction) {
        if(System.currentTimeMillis()<testSchedule.endTime)
        listOfALLTransactions.add(transaction);
    }

    public static synchronized void pushBlockReport(HashMap<BigInteger,CustomBlock> blockList) {
        Report.blockListFromMonitor=blockList;
        System.out.println("Report: Number of Blocks Received: "+Report.blockListFromMonitor.keySet().size());
    }

    public static void calculateRoundTransactionLatency() {
    //Calculate Transaction Latency for bidding
        double transactionLatency;
        double sumOfTransactionLatency;
        RoundResult biddingRoundResult;
        RoundResult marketClearanceRoundResult;
        RoundResult paymentSettlementRoundResult;
        RoundResult getDipatchedEnergytRoundResult;
        RoundResult getBalanceRoundResult;
        for(int r=0; r<testSchedule.numberOfRounds;r++){
            biddingRoundResult = biddingRoundResults.get(r);
            marketClearanceRoundResult = marketClearanceRoundResults.get(r);
            paymentSettlementRoundResult=paymentSettlementRoundResults.get(r);
            getDipatchedEnergytRoundResult=getDispatchedEnergyRoundResults.get(r);
           getBalanceRoundResult=getBalanceRoundResults.get(r);
            sumOfTransactionLatency=0;
         for(Transaction tr: biddingTransactionList ){
             if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                 transactionLatency=tr.confimationTime-tr.submissionTime;
                // System.out.println("Latency: "+transactionLatency);
                  if(transactionLatency < biddingRoundResult.roundLatency[0]) {//If this transaction's latency is < the minmimum latency for this round
                     biddingRoundResult.roundLatency[0] = transactionLatency;
                 }

                 if(transactionLatency > biddingRoundResult.roundLatency[1]) {//If this transaction's latency is > the maximum latency for this round
                     biddingRoundResult.roundLatency[1] = transactionLatency;
                 }
                sumOfTransactionLatency += transactionLatency;

             }

            }
            biddingRoundResult.roundLatency[2]= sumOfTransactionLatency/biddingRoundResult.numberOfSuccessfulTransactions; // The average transaction latency

            sumOfTransactionLatency=0;
            for(QueryTransaction tr: getEnergyDispatchTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    transactionLatency=tr.responseTime-tr.submissionTime;
                    // System.out.println("Latency: "+transactionLatency);
                    if(transactionLatency < getDipatchedEnergytRoundResult.roundLatency[0]) {//If this transaction's latency is < the minmimum latency for this round
                        getDipatchedEnergytRoundResult.roundLatency[0] = transactionLatency;
                    }

                    if(transactionLatency > getDipatchedEnergytRoundResult.roundLatency[1]) {//If this transaction's latency is > the maximum latency for this round
                        getDipatchedEnergytRoundResult.roundLatency[1] = transactionLatency;
                    }
                    sumOfTransactionLatency += transactionLatency;

                }

            }
            getDipatchedEnergytRoundResult.roundLatency[2]= sumOfTransactionLatency/getDipatchedEnergytRoundResult.numberOfSuccessfulTransactions; // The average transaction latency


            sumOfTransactionLatency=0;
            for(QueryTransaction tr: getBalanceTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    transactionLatency=tr.responseTime-tr.submissionTime;
                    // System.out.println("Latency: "+transactionLatency);
                    if(transactionLatency < getBalanceRoundResult.roundLatency[0]) {//If this transaction's latency is < the minmimum latency for this round
                        getBalanceRoundResult.roundLatency[0] = transactionLatency;
                    }

                    if(transactionLatency > getBalanceRoundResult.roundLatency[1]) {//If this transaction's latency is > the maximum latency for this round
                        getBalanceRoundResult.roundLatency[1] = transactionLatency;
                    }
                    sumOfTransactionLatency += transactionLatency;

                }

            }
            getBalanceRoundResult.roundLatency[2]= sumOfTransactionLatency/getBalanceRoundResult.numberOfSuccessfulTransactions; // The average transaction latency


            for(Transaction tr: marketClearanceTransactionList ){// The minimum, maximum and the average transaction latency is the same for market clearance as we only have one transaction per round
                if(!(tr.Type==Constants.TransactionType.PM_MarketClearance))
                    System.out.println(" Not Market Clearance Transaction");
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    transactionLatency=tr.confimationTime-tr.submissionTime;
                    marketClearanceRoundResult.roundLatency[0] = transactionLatency;
                    marketClearanceRoundResult.roundLatency[1] = transactionLatency;
                    marketClearanceRoundResult.roundLatency[2] = transactionLatency;
                }

            }

            for(Transaction tr: paymentSettlementTransactionList ){// The minimum, maximum and the average transaction latency is the same for payment settlement as we only have one transaction per round
                if(!(tr.Type==Constants.TransactionType.PS_SettlingPayments))
                    System.out.println(" Not Bidding Transaction");
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    transactionLatency=tr.confimationTime-tr.submissionTime;
                    paymentSettlementRoundResult.roundLatency[0] = transactionLatency;
                    paymentSettlementRoundResult.roundLatency[1] = transactionLatency;
                    paymentSettlementRoundResult.roundLatency[2] = transactionLatency;
                }

            }
        }


    }


    public static void calculateRoundThroughtput() {
        double firstSubmissionTimeForTheRound; // The first time that a transaction is submitted for the round
        double lastConfirmationTimeForTheRound; // The last time that a transaction is confirmed for the round
        double lastSubmissionTimeForTheRound;
        double submissionTime;
        double confirmationTime;
        RoundResult biddingRoundResult;
        RoundResult marketClearanceRoundResult;
        RoundResult paymentSettlementRoundResult;
        RoundResult getDispatchedEnergyRoundResult;
        RoundResult getBalanceRoundResult;

        for(int r=0; r<testSchedule.numberOfRounds;r++){
            biddingRoundResult= biddingRoundResults.get(r);
            marketClearanceRoundResult= marketClearanceRoundResults.get(r);
            paymentSettlementRoundResult=paymentSettlementRoundResults.get(r);
            getDispatchedEnergyRoundResult=getDispatchedEnergyRoundResults.get(r);
            getBalanceRoundResult=getBalanceRoundResults.get(r);

            firstSubmissionTimeForTheRound=testSchedule.endTime;
            lastConfirmationTimeForTheRound= testSchedule.startTime;
            lastSubmissionTimeForTheRound=testSchedule.startTime;
              for(Transaction tr: biddingTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    submissionTime=tr.submissionTime;
                    confirmationTime=tr.confimationTime;
                 //   System.out.println("Round: "+tr.round+"Submission Time: "+ submissionTime+" Confirmation Time: "+confirmationTime);
                    if(submissionTime < firstSubmissionTimeForTheRound){
                        firstSubmissionTimeForTheRound=submissionTime;
                    }
                    if(submissionTime > lastSubmissionTimeForTheRound){
                        lastSubmissionTimeForTheRound=submissionTime;
                    }

                    if(confirmationTime > lastConfirmationTimeForTheRound){
                        lastConfirmationTimeForTheRound=confirmationTime;
                    }

                }

            }
            biddingRoundResult.lastSubmissionTimeMinusFirstSubmissionTime=lastSubmissionTimeForTheRound-firstSubmissionTimeForTheRound;
            biddingRoundResult.lastConfirmationTimeMinusFirstSubmissionTime=lastConfirmationTimeForTheRound-firstSubmissionTimeForTheRound;
            System.out.println("FirstSubmissionTimeForTheRound for for Bidding: "+firstSubmissionTimeForTheRound +" lastSubmissionTimeForTheRound for Bidding: "+lastSubmissionTimeForTheRound+" lastConfirmationTimeForTheRound for Bidding: "+ lastConfirmationTimeForTheRound
            + " LST -FST "+ biddingRoundResult.lastSubmissionTimeMinusFirstSubmissionTime);

            System.out.println("Number of Successful Transactions for Bidding: "+biddingRoundResult.numberOfSuccessfulTransactions);
            biddingRoundResult.roundThroughput=(biddingRoundResult.numberOfSuccessfulTransactions/(lastConfirmationTimeForTheRound-firstSubmissionTimeForTheRound))*1000;// Throughput is in seconds
           // This calculations is wrong . Sending rate should be calculated based on the first and last submiccion time for all transactions not just successful transactions.
            biddingRoundResult.sendingRate=((biddingRoundResult.numberOfSuccessfulTransactions +biddingRoundResult.numberOfFailedTransactions)/(lastSubmissionTimeForTheRound-firstSubmissionTimeForTheRound))*1000;
            System.out.println("Sending Rate for Bidding: "+ biddingRoundResult.sendingRate);

            firstSubmissionTimeForTheRound=testSchedule.endTime;
            lastConfirmationTimeForTheRound= testSchedule.startTime;
            lastSubmissionTimeForTheRound=testSchedule.startTime;
            for(QueryTransaction tr: getEnergyDispatchTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    submissionTime=tr.submissionTime;
                    confirmationTime=tr.responseTime;
                    //   System.out.println("Round: "+tr.round+"Submission Time: "+ submissionTime+" Confirmation Time: "+confirmationTime);
                    if(submissionTime < firstSubmissionTimeForTheRound){
                        firstSubmissionTimeForTheRound=submissionTime;
                    }
                    if(submissionTime > lastSubmissionTimeForTheRound){
                        lastSubmissionTimeForTheRound=submissionTime;
                    }

                    if(confirmationTime > lastConfirmationTimeForTheRound){
                        lastConfirmationTimeForTheRound=confirmationTime;
                    }

                }

            }
            System.out.println("FirstSubmissionTimeForTheRound for Dispatched Energy: "+firstSubmissionTimeForTheRound +" lastSubmissionTimeForTheRound for Dispatched Energy: "+lastSubmissionTimeForTheRound+" lastConfirmationTimeForTheRound for Dispatched Energy: "+ lastConfirmationTimeForTheRound);
            System.out.println("Number of Succcessful Transactions for Dispatched Energy: "+getDispatchedEnergyRoundResult.numberOfSuccessfulTransactions);
            getDispatchedEnergyRoundResult.roundThroughput=(getDispatchedEnergyRoundResult.numberOfSuccessfulTransactions/(lastConfirmationTimeForTheRound-firstSubmissionTimeForTheRound))*1000;// Throughput is in seconds
            getDispatchedEnergyRoundResult.sendingRate=((getDispatchedEnergyRoundResult.numberOfSuccessfulTransactions +getDispatchedEnergyRoundResult.numberOfFailedTransactions)/(lastSubmissionTimeForTheRound-firstSubmissionTimeForTheRound))*1000;
            System.out.println("Sending Rate for Dispatched Energy: "+ getDispatchedEnergyRoundResult.sendingRate);


            firstSubmissionTimeForTheRound=testSchedule.endTime;
            lastConfirmationTimeForTheRound= testSchedule.startTime;
            lastSubmissionTimeForTheRound=testSchedule.startTime;
            for(QueryTransaction tr: getBalanceTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    submissionTime=tr.submissionTime;
                    confirmationTime=tr.responseTime;
                    //   System.out.println("Round: "+tr.round+"Submission Time: "+ submissionTime+" Confirmation Time: "+confirmationTime);
                    if(submissionTime < firstSubmissionTimeForTheRound){
                        firstSubmissionTimeForTheRound=submissionTime;
                    }
                    if(submissionTime > lastSubmissionTimeForTheRound){
                        lastSubmissionTimeForTheRound=submissionTime;
                    }

                    if(confirmationTime > lastConfirmationTimeForTheRound){
                        lastConfirmationTimeForTheRound=confirmationTime;
                    }

                }

            }
            getBalanceRoundResult.lastSubmissionTimeMinusFirstSubmissionTime=lastSubmissionTimeForTheRound-firstSubmissionTimeForTheRound;
            getBalanceRoundResult.lastConfirmationTimeMinusFirstSubmissionTime=lastConfirmationTimeForTheRound-firstSubmissionTimeForTheRound;
            System.out.println("FirstSubmissionTimeForTheRound for Get Balance: "+firstSubmissionTimeForTheRound +" lastSubmissionTimeForTheRound for Get Balance: "+lastSubmissionTimeForTheRound+" lastConfirmationTimeForTheRound for Get Balance: "+ lastConfirmationTimeForTheRound);
            System.out.println("Number of Successful Transactions for Get Balance: "+getBalanceRoundResult.numberOfSuccessfulTransactions);
            getBalanceRoundResult.roundThroughput=(getBalanceRoundResult.numberOfSuccessfulTransactions/(lastConfirmationTimeForTheRound-firstSubmissionTimeForTheRound))*1000;// Throughput is in seconds
            getBalanceRoundResult.sendingRate=((getBalanceRoundResult.numberOfSuccessfulTransactions +getBalanceRoundResult.numberOfFailedTransactions)/(lastSubmissionTimeForTheRound-firstSubmissionTimeForTheRound))*1000;
            System.out.println("Sending Rate for Get Balance: "+ getBalanceRoundResult.sendingRate);


            //Since we know there is only one transaction per round, it doesn't make sense to calculate throughput for only one transaction.
            marketClearanceRoundResult.roundThroughput=1;
            marketClearanceRoundResult.sendingRate=1; //Since we know there is only one transaction per round sending rate is 1 and (lastSubmissionTimeForTheRound-firstSubmissionTimeForTheRound) is zero

            ////Since we know there is only one transaction per round, it doesn't make sense to calculate throughput for only one transaction.
            paymentSettlementRoundResult.roundThroughput=1;
            paymentSettlementRoundResult.sendingRate=1;

        }
    }

    public static void calculateGasUsed() {
        //Calculate the average gas used for each round
        double sumOfGasUSed=0;
        RoundResult biddingRoundResult;
        RoundResult marketClearanceRoundResult;
        RoundResult paymentSettlementRoundResult;
        double gasUsedforFirstTransaction=0;

        for(int r=0; r<testSchedule.numberOfRounds;r++){
            biddingRoundResult = biddingRoundResults.get(r);
            marketClearanceRoundResult = marketClearanceRoundResults.get(r);
            paymentSettlementRoundResult=paymentSettlementRoundResults.get(r);
            sumOfGasUSed=0;
            int counter=0;
            for(Transaction tr: biddingTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    if (counter==0){
                        sumOfGasUSed+=0; //I am not considering the first transaction as it has some additional initial cost
                    }
                    else{
                        sumOfGasUSed+=tr.gasUSed;
                    }
                }
            counter++;
            }
            biddingRoundResult.averageGasUsed= sumOfGasUSed/(biddingRoundResult.numberOfSuccessfulTransactions-1); // //I am subtracting one bcs ia am not considering the first transaction as it has some additional initial cost

            sumOfGasUSed=0;
            for(Transaction tr: marketClearanceTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                        sumOfGasUSed+=tr.gasUSed;
                  //  System.out.println("Round: "+r+" Market Clearance Transaction Gas Used: "+tr.gasUSed);
                }
            }
            marketClearanceRoundResult.averageGasUsed= sumOfGasUSed/marketClearanceRoundResult.numberOfSuccessfulTransactions; // The average gas used

            sumOfGasUSed=0;
            for(Transaction tr: paymentSettlementTransactionList ){
                if (tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    sumOfGasUSed+=tr.gasUSed;
                    //  System.out.println("Round: "+r+" Market Clearance Transaction Gas Used: "+tr.gasUSed);
                }
            }
            paymentSettlementRoundResult.averageGasUsed= sumOfGasUSed/paymentSettlementRoundResult.numberOfSuccessfulTransactions; // The average gas used


        }


    }

    public static void calculateSummaryResult() {
        double overallMinimumLatency = 1000000000; // may be need to replace with better number
        double overallMaximumLatency = 0; // may be need to replace with better number
        double sumOfAverageRoundlLatencies=0; // Used to calculate the overall average latency
        double sumOfAverageRoundThroughputs=0;
        double sumOfAverageGasUsed=0;
    for(RoundResult rs: biddingRoundResults){
        biddingSummaryResult.numberOfSuccessfulTransactions+= rs.numberOfSuccessfulTransactions;
        biddingSummaryResult.numberOfFailedTransactions+= rs.numberOfFailedTransactions;
        if(rs.roundLatency[0] < overallMinimumLatency){
            overallMinimumLatency=rs.roundLatency[0];
        }
        if(rs.roundLatency[1] > overallMaximumLatency){
            overallMaximumLatency=rs.roundLatency[1];
        }
        sumOfAverageRoundlLatencies +=rs.roundLatency[2];
        sumOfAverageRoundThroughputs+=rs.roundThroughput;
        sumOfAverageGasUsed+=rs.averageGasUsed;
    }
        biddingSummaryResult.overallLatency[0]=overallMinimumLatency;
        biddingSummaryResult.overallLatency[1]=overallMaximumLatency;
        biddingSummaryResult.overallLatency[2]=sumOfAverageRoundlLatencies/testSchedule.numberOfRounds; //Overall average Latency
        biddingSummaryResult.overallThroughPut=sumOfAverageRoundThroughputs/testSchedule.numberOfRounds; //Overall Average Throughput
        biddingSummaryResult.overAllAverageGasUsed=sumOfAverageGasUsed/testSchedule.numberOfRounds;

        overallMinimumLatency = 1000000000; // may be need to replace with better number
        overallMaximumLatency = 0; // may be need to replace with better number
        sumOfAverageRoundlLatencies=0; // Used to calculate the overall average latency
        sumOfAverageRoundThroughputs=0;
        for(RoundResult rs: getDispatchedEnergyRoundResults){
            getDispatchedEnergySummaryResult.numberOfSuccessfulTransactions+= rs.numberOfSuccessfulTransactions;
            getDispatchedEnergySummaryResult.numberOfFailedTransactions+= rs.numberOfFailedTransactions;
            if(rs.roundLatency[0] < overallMinimumLatency){
                overallMinimumLatency=rs.roundLatency[0];
            }
            if(rs.roundLatency[1] > overallMaximumLatency){
                overallMaximumLatency=rs.roundLatency[1];
            }
            sumOfAverageRoundlLatencies +=rs.roundLatency[2];
            sumOfAverageRoundThroughputs+=rs.roundThroughput;
            sumOfAverageGasUsed+=rs.averageGasUsed;
        }
        getDispatchedEnergySummaryResult.overallLatency[0]=overallMinimumLatency;
        getDispatchedEnergySummaryResult.overallLatency[1]=overallMaximumLatency;
        getDispatchedEnergySummaryResult.overallLatency[2]=sumOfAverageRoundlLatencies/testSchedule.numberOfRounds; //Overall average Latency
        getDispatchedEnergySummaryResult.overallThroughPut=sumOfAverageRoundThroughputs/testSchedule.numberOfRounds; //Overall Average Throughput

        overallMinimumLatency = 1000000000; // may be need to replace with better number
        overallMaximumLatency = 0; // may be need to replace with better number
        sumOfAverageRoundlLatencies=0; // Used to calculate the overall average latency
        sumOfAverageRoundThroughputs=0;
        for(RoundResult rs: getBalanceRoundResults){
            getBalanceSummaryResult.numberOfSuccessfulTransactions+= rs.numberOfSuccessfulTransactions;
            getBalanceSummaryResult.numberOfFailedTransactions+= rs.numberOfFailedTransactions;
            if(rs.roundLatency[0] < overallMinimumLatency){
                overallMinimumLatency=rs.roundLatency[0];
            }
            if(rs.roundLatency[1] > overallMaximumLatency){
                overallMaximumLatency=rs.roundLatency[1];
            }
            sumOfAverageRoundlLatencies +=rs.roundLatency[2];
            sumOfAverageRoundThroughputs+=rs.roundThroughput;
        }
        getBalanceSummaryResult.overallLatency[0]=overallMinimumLatency;
        getBalanceSummaryResult.overallLatency[1]=overallMaximumLatency;
        getBalanceSummaryResult.overallLatency[2]=sumOfAverageRoundlLatencies/testSchedule.numberOfRounds; //Overall average Latency
        getBalanceSummaryResult.overallThroughPut=sumOfAverageRoundThroughputs/testSchedule.numberOfRounds; //Overall Average Throughput

        overallMinimumLatency = 1000000000; // may be need to replace with better number
        overallMaximumLatency = 0; // may be need to replace with better number
        sumOfAverageRoundlLatencies=0; // Used to calculate the overall average latency
        sumOfAverageRoundThroughputs=0;
        sumOfAverageGasUsed=0;
        for(RoundResult rs: marketClearanceRoundResults){
            marketClearanceSummaryResult.numberOfSuccessfulTransactions+=rs.numberOfSuccessfulTransactions;
            marketClearanceSummaryResult.numberOfFailedTransactions+= rs.numberOfFailedTransactions;
            if(rs.roundLatency[0] < overallMinimumLatency){
                overallMinimumLatency=rs.roundLatency[0];
            }
            if(rs.roundLatency[1] > overallMaximumLatency){
                overallMaximumLatency=rs.roundLatency[1];
            }
            sumOfAverageRoundlLatencies +=rs.roundLatency[2];
            sumOfAverageRoundThroughputs+=rs.roundThroughput;
            sumOfAverageGasUsed+=rs.averageGasUsed;
        }
        marketClearanceSummaryResult.overallLatency[0]=overallMinimumLatency;
        marketClearanceSummaryResult.overallLatency[1]=overallMaximumLatency;
        marketClearanceSummaryResult.overallLatency[2]=sumOfAverageRoundlLatencies/testSchedule.numberOfRounds; //Overall average Latency
        marketClearanceSummaryResult.overallThroughPut=sumOfAverageRoundThroughputs/testSchedule.numberOfRounds; //Overall Average Throughput
        marketClearanceSummaryResult.overAllAverageGasUsed=sumOfAverageGasUsed/testSchedule.numberOfRounds;

        overallMinimumLatency = 1000000000; // may be need to replace with better number
        overallMaximumLatency = 0; // may be need to replace with better number
        sumOfAverageRoundlLatencies=0; // Used to calculate the overall average latency
        sumOfAverageRoundThroughputs=0;
        sumOfAverageGasUsed=0;
        for(RoundResult rs: paymentSettlementRoundResults){
            paymentSettlementSummaryResult.numberOfSuccessfulTransactions+=rs.numberOfSuccessfulTransactions;
            paymentSettlementSummaryResult.numberOfFailedTransactions+= rs.numberOfFailedTransactions;
            if(rs.roundLatency[0] < overallMinimumLatency){
                overallMinimumLatency=rs.roundLatency[0];
            }
            if(rs.roundLatency[1] > overallMaximumLatency){
                overallMaximumLatency=rs.roundLatency[1];
            }
            sumOfAverageRoundlLatencies +=rs.roundLatency[2];
            sumOfAverageRoundThroughputs+=rs.roundThroughput;
            sumOfAverageGasUsed+=rs.averageGasUsed;
        }
        paymentSettlementSummaryResult.overallLatency[0]=overallMinimumLatency;
        paymentSettlementSummaryResult.overallLatency[1]=overallMaximumLatency;
        paymentSettlementSummaryResult.overallLatency[2]=sumOfAverageRoundlLatencies/testSchedule.numberOfRounds; //Overall average Latency
        paymentSettlementSummaryResult.overallThroughPut=sumOfAverageRoundThroughputs/testSchedule.numberOfRounds; //Overall Average Throughput
        paymentSettlementSummaryResult.overAllAverageGasUsed=sumOfAverageGasUsed/testSchedule.numberOfRounds;

    }

    public static void printReport (){
        System.out.println("PM Bidding ROUND RESULTs==============================");
        for(RoundResult rs: biddingRoundResults){
            System.out.println("Round: "+rs.round+ " Type: "+rs.transactionType+" Sending rate: "+rs.sendingRate+" Number Of Successful Transactions: "+rs.numberOfSuccessfulTransactions+
                            " Number of Failed Transactions: "+rs.numberOfFailedTransactions+ " Minimum Latency: "+rs.roundLatency[0]+" " +
                            " Maximum Latency: "+rs.roundLatency[1]+ " Average Latency: "+rs.roundLatency[2]+ " Average Throughput: " +rs.roundThroughput);
        }

        System.out.println(" PM Bidding Summary Results=============================");
        System.out.println(" Sending rate: "+biddingSummaryResult.sendingRate+ " Number Of Successful Transactions: "+biddingSummaryResult.numberOfSuccessfulTransactions+ " Number of Failed Transactions: "+biddingSummaryResult.numberOfFailedTransactions+
                " Minimum Latency: "+biddingSummaryResult.overallLatency[0]+ " Maximum Latency: "+biddingSummaryResult.overallLatency[1]+
                " Average Latency: "+biddingSummaryResult.overallLatency[2]+ " Average Throughput: " +biddingSummaryResult.overallThroughPut);

        System.out.println("Get Dispatched Energy ROUND RESULTs==============================");
        for(RoundResult rs: getDispatchedEnergyRoundResults){
            System.out.println("Round: "+rs.round+ " Type: "+rs.transactionType+" Sending rate: "+rs.sendingRate+" Number Of Successful Transactions: "+rs.numberOfSuccessfulTransactions+
                    " Number of Failed Transactions: "+rs.numberOfFailedTransactions+ " Minimum Latency: "+rs.roundLatency[0]+" " +
                    " Maximum Latency: "+rs.roundLatency[1]+ " Average Latency: "+rs.roundLatency[2]+ " Average Throughput: " +rs.roundThroughput);
        }

        System.out.println(" Get Dispatched Energy Summary Results=============================");
        System.out.println(" Sending rate: "+getDispatchedEnergySummaryResult.sendingRate+ " Number Of Successful Transactions: "+getDispatchedEnergySummaryResult.numberOfSuccessfulTransactions+ " Number of Failed Transactions: "+getDispatchedEnergySummaryResult.numberOfFailedTransactions+
                " Minimum Latency: "+getDispatchedEnergySummaryResult.overallLatency[0]+ " Maximum Latency: "+getDispatchedEnergySummaryResult.overallLatency[1]+
                " Average Latency: "+getDispatchedEnergySummaryResult.overallLatency[2]+ " Average Throughput: " +getDispatchedEnergySummaryResult.overallThroughPut);

        System.out.println("Get Balance ROUND RESULTs==============================");
        for(RoundResult rs: getBalanceRoundResults){
            System.out.println("Round: "+rs.round+ " Type: "+rs.transactionType+" Sending rate: "+rs.sendingRate+" Number Of Successful Transactions: "+rs.numberOfSuccessfulTransactions+
                    " Number of Failed Transactions: "+rs.numberOfFailedTransactions+ " Minimum Latency: "+rs.roundLatency[0]+" " +
                    " Maximum Latency: "+rs.roundLatency[1]+ " Average Latency: "+rs.roundLatency[2]+ " Average Throughput: " +rs.roundThroughput);
        }

        System.out.println(" Get Balance Summary Results=============================");
        System.out.println(" Sending rate: "+getBalanceSummaryResult.sendingRate+ " Number Of Successful Transactions: "+getBalanceSummaryResult.numberOfSuccessfulTransactions+ " Number of Failed Transactions: "+getBalanceSummaryResult.numberOfFailedTransactions+
                " Minimum Latency: "+getBalanceSummaryResult.overallLatency[0]+ " Maximum Latency: "+getBalanceSummaryResult.overallLatency[1]+
                " Average Latency: "+getBalanceSummaryResult.overallLatency[2]+ " Average Throughput: " +getBalanceSummaryResult.overallThroughPut);

        System.out.println("PM Market Clearance ROUND RESULTs==============================");
        for(RoundResult rs: marketClearanceRoundResults){
            System.out.println("Round: "+rs.round+" Type: "+rs.transactionType+" Sending rate: "+rs.sendingRate+" Number Of Successful Transactions: "+rs.numberOfSuccessfulTransactions+
                    " Number of Failed Transactions: "+rs.numberOfFailedTransactions+ " Minimum Latency: "+rs.roundLatency[0]+" " +
                    " Maximum Latency: "+rs.roundLatency[1]+ " Average Latency: "+rs.roundLatency[2]+" Average Throughput: " +rs.roundThroughput);
        }
        System.out.println(" PM Market Clearance Summary Results=============================");
        System.out.println("Sending rate: "+marketClearanceSummaryResult.sendingRate+" Number Of Successful Transactions: "+marketClearanceSummaryResult.numberOfSuccessfulTransactions+ " Number of Failed Transactions: "+marketClearanceSummaryResult.numberOfFailedTransactions+
                " Minimum Latency: "+marketClearanceSummaryResult.overallLatency[0]+ " Maximum Latency: "+marketClearanceSummaryResult.overallLatency[1]+
                " Average Latency: "+marketClearanceSummaryResult.overallLatency[2]+ " Average Throughput: " +marketClearanceSummaryResult.overallThroughPut);

        //We don't print throughput for market clearance as it doesn't make sense to calculate throughput for market clearance that has only one transaction.
        System.out.println("PM Payment Settlement ROUND RESULTs==============================");
        for(RoundResult rs: paymentSettlementRoundResults){
            System.out.println("Round: "+rs.round+" Type: "+rs.transactionType+" Sending rate: "+rs.sendingRate+" Number Of Successful Transactions: "+rs.numberOfSuccessfulTransactions+
                    " Number of Failed Transactions: "+rs.numberOfFailedTransactions+ " Minimum Latency: "+rs.roundLatency[0]+" " +
                    " Maximum Latency: "+rs.roundLatency[1]+ " Average Latency: "+rs.roundLatency[2] +" Average Throughput: " +rs.roundThroughput);
        }
        System.out.println(" PM Payment Settlement Summary Results=============================");
        System.out.println("Sending rate: "+paymentSettlementSummaryResult.sendingRate+" Number Of Successful Transactions: "+paymentSettlementSummaryResult.numberOfSuccessfulTransactions+ " Number of Failed Transactions: "+paymentSettlementSummaryResult.numberOfFailedTransactions+
                " Minimum Latency: "+paymentSettlementSummaryResult.overallLatency[0]+ " Maximum Latency: "+paymentSettlementSummaryResult.overallLatency[1]+
                " Average Latency: "+paymentSettlementSummaryResult.overallLatency[2]+ " Average Throughput: " +paymentSettlementSummaryResult.overallThroughPut);
    }

    public static void writeToFile() {
        String[] columns = {"Round", "Sending Rate", "No of Successful Transactions", "No of Failed Transactions", "MinLatency", "MaxLatency", "AveLatency", "Throughput","Ave GasUSed", "lastSubmissionTimeMinusFirstSubmissionTime","lastConfirmationTimeMinusFirstSubmissionTIme"};
        Workbook workbook = new XSSFWorkbook();


        Sheet biddingSheet = workbook.createSheet("PM Bidding Summary");
        Sheet marketClearanceSheet = workbook.createSheet("PM Market Clearance");
        Sheet paymentSettlementSheet = workbook.createSheet("PM Payment Settlement");
        Sheet getDispatchedEnergySheet = workbook.createSheet("Get Dispatched Energy");
        Sheet getBalanceSheet = workbook.createSheet("Get Balance");
        Sheet biddingTransactionDetailsSheet = workbook.createSheet("Bidding Transaction Details");
        Sheet getBalanceTransactionDetailsSheet = workbook.createSheet("GetBalance Transaction Details");

        // Create Header Row
        Row biddingHeaderRow = biddingSheet.createRow(0);
        Row marketClearanceHeaderRow = marketClearanceSheet.createRow(0);
        Row paymentSettlementHeaderRow = paymentSettlementSheet.createRow(0);
        Row getDisptchedEnergyHeaderRow = getDispatchedEnergySheet.createRow(0);
        Row getBalanceHeaderRow = getBalanceSheet.createRow(0);
        Row biddingTransactionDetailsHeaderRow= biddingTransactionDetailsSheet.createRow(0);
        Row getBalanceTransactionDetailsHeaderRow= getBalanceTransactionDetailsSheet.createRow(0);

        // Create a CellStyle and a font
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create header cells for bidding sheet
        for(int i = 0; i < columns.length; i++) {
            Cell cell = biddingHeaderRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        // Create header cells for market clearance sheet
        for(int i = 0; i < columns.length; i++) {
            Cell cell = marketClearanceHeaderRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create header cells for payment Settlement sheet
        for(int i = 0; i < columns.length; i++) {
            Cell cell = paymentSettlementHeaderRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create header cells for get Dispatched Energy
        for(int i = 0; i < columns.length; i++) {
            Cell cell = getDisptchedEnergyHeaderRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create header cells for get balance sheet
        for(int i = 0; i < columns.length; i++) {
            Cell cell = getBalanceHeaderRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create header cells for bidding transaction details
        for(int r=0;r<testSchedule.numberOfRounds;r++){
            Cell cell = biddingTransactionDetailsHeaderRow.createCell(4*r);
            cell.setCellValue("Round"+String.valueOf(r));
            cell.setCellStyle(headerCellStyle);
        }

        // Create header cells for get balance transaction details
        for(int r=0;r<testSchedule.numberOfRounds;r++){
            Cell cell = getBalanceTransactionDetailsHeaderRow.createCell(4*r);
            cell.setCellValue("Round"+String.valueOf(r));
            cell.setCellStyle(headerCellStyle);
        }

// Writing bidding transaction details to file
        int numberOfRows=biddingRoundResults.get(testSchedule.numberOfRounds-1).numberOfSuccessfulTransactions +biddingRoundResults.get(testSchedule.numberOfRounds-1).numberOfFailedTransactions;
        ArrayList<Row> rows= new ArrayList<>();
        for(int row=0;row<numberOfRows ;row++){
            rows.add(biddingTransactionDetailsSheet.createRow(row+1));
        }

        int rowNumber=0;
        for(int r=0;r<testSchedule.numberOfRounds;r++){
            int firstColumnForTheRound= 4*r;
            rowNumber=0; // The first element is actually the second row in the sheet
            for(Transaction tr:biddingTransactionList){
                if(tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    Cell submissionTime = rows.get(rowNumber).createCell(firstColumnForTheRound);
                    submissionTime.setCellValue(tr.submissionTime);
                    Cell confirmationTime= rows.get(rowNumber).createCell(firstColumnForTheRound+1);
                    confirmationTime.setCellValue(tr.confimationTime);
                    Cell differenceBetweenTheTwo= rows.get(rowNumber).createCell(firstColumnForTheRound+2);
                    differenceBetweenTheTwo.setCellValue(tr.confimationTime-tr.submissionTime);
                    rowNumber++;
                }
            }
        }

        // Writing get balance query transaction details to file
        numberOfRows=getBalanceRoundResults.get(testSchedule.numberOfRounds-1).numberOfSuccessfulTransactions +getBalanceRoundResults.get(testSchedule.numberOfRounds-1).numberOfFailedTransactions;
        ArrayList<Row> rows2= new ArrayList<>();
        for(int row=0;row< numberOfRows;row++){
            rows2.add(getBalanceTransactionDetailsSheet.createRow(row+1));
        }

        int rowNumber2=0;
        for(int r=0;r<testSchedule.numberOfRounds;r++){
            int firstColumnForTheRound= 4*r;
            rowNumber2=0; // The first element is actually the second row in the sheet
            for(QueryTransaction tr:getBalanceTransactionList){
                if(tr.round==r && tr.Status==Constants.TransactionStatus.SUCCESS){
                    Cell submissionTime = rows2.get(rowNumber2).createCell(firstColumnForTheRound);
                    submissionTime.setCellValue(tr.submissionTime);
                    Cell confirmationTime= rows2.get(rowNumber2).createCell(firstColumnForTheRound+1);
                    confirmationTime.setCellValue(tr.responseTime);
                    Cell differenceBetweenTheTwo= rows2.get(rowNumber2).createCell(firstColumnForTheRound+2);
                    differenceBetweenTheTwo.setCellValue(tr.responseTime-tr.submissionTime);
                    rowNumber2++;
                }
            }
        }



        // Write Bidding Round Result
        for(RoundResult rs: biddingRoundResults){//This assumes that the round results are arranged in order inside biddingRoundResults arraylist. i.e the first element is round 0
            Row row= biddingSheet.createRow(rs.round+1); //The first line should come on the second line as the header appears on the first
            for(int col=0;col<columns.length;col++){
                Cell cell = row.createCell(col);
                switch(col) {
                    case 0: cell.setCellValue(rs.round);
                    break;
                    case 1: cell.setCellValue(rs.sendingRate);
                        break;
                    case 2:  cell.setCellValue(rs.numberOfSuccessfulTransactions);
                        break;
                    case 3:  cell.setCellValue(rs.numberOfFailedTransactions);
                        break;
                    case 4:  cell.setCellValue(rs.roundLatency[0]);
                        break;
                    case 5:  cell.setCellValue(rs.roundLatency[1]);
                        break;
                    case 6:  cell.setCellValue(rs.roundLatency[2]);
                        break;
                    case 7:  cell.setCellValue(rs.roundThroughput);
                        break;
                    case 8:  cell.setCellValue(rs.averageGasUsed);
                        break;
                    case 9:  cell.setCellValue(rs.lastSubmissionTimeMinusFirstSubmissionTime);
                        break;
                    case 10:  cell.setCellValue(rs.lastConfirmationTimeMinusFirstSubmissionTime);
                        break;
                    default: cell.setCellValue("-");
                        break;
                }
            }
        }

        //Write Bidding Summary Result
        Row biddingSummaryRow=biddingSheet.createRow(biddingRoundResults.size()+1);
        for(int col=0;col<columns.length;col++){
            Cell cell = biddingSummaryRow.createCell(col);
            switch(col) {
                case 0: cell.setCellValue("Summary");
                    break;
                case 1: cell.setCellValue(biddingSummaryResult.sendingRate);
                    break;
                case 2:  cell.setCellValue(biddingSummaryResult.numberOfSuccessfulTransactions);
                    break;
                case 3:  cell.setCellValue(biddingSummaryResult.numberOfFailedTransactions);
                    break;
                case 4:  cell.setCellValue(biddingSummaryResult.overallLatency[0]);
                    break;
                case 5:  cell.setCellValue(biddingSummaryResult.overallLatency[1]);
                    break;
                case 6:  cell.setCellValue(biddingSummaryResult.overallLatency[2]);
                    break;
                case 7:  cell.setCellValue(biddingSummaryResult.overallThroughPut);
                    break;
                case 8:  cell.setCellValue(biddingSummaryResult.overAllAverageGasUsed);
                    break;
                default: cell.setCellValue("-");
                    break;
            }
        }


        // Write Get Dispatched Energy Round Result
        for(RoundResult rs: getDispatchedEnergyRoundResults){//This assumes that the round results are arranged in order inside biddingRoundResults arraylist. i.e the first element is round 0
            Row row= getDispatchedEnergySheet.createRow(rs.round+1); //The first line should come on the second line as the header appears on the first
            for(int col=0;col<columns.length;col++){
                Cell cell = row.createCell(col);
                switch(col) {
                    case 0: cell.setCellValue(rs.round);
                        break;
                    case 1: cell.setCellValue(rs.sendingRate);
                        break;
                    case 2:  cell.setCellValue(rs.numberOfSuccessfulTransactions);
                        break;
                    case 3:  cell.setCellValue(rs.numberOfFailedTransactions);
                        break;
                    case 4:  cell.setCellValue(rs.roundLatency[0]);
                        break;
                    case 5:  cell.setCellValue(rs.roundLatency[1]);
                        break;
                    case 6:  cell.setCellValue(rs.roundLatency[2]);
                        break;
                    case 7:  cell.setCellValue(rs.roundThroughput);
                        break;
                    default: cell.setCellValue("-");
                        break;
                }
            }
        }

        //Write Get Dispatched Energy Summary Result
        Row getDispatchedEnergySummaryRow=getDispatchedEnergySheet.createRow(getDispatchedEnergyRoundResults.size()+1);
        for(int col=0;col<columns.length;col++){
            Cell cell = getDispatchedEnergySummaryRow.createCell(col);
            switch(col) {
                case 0: cell.setCellValue("Summary");
                    break;
                case 1: cell.setCellValue(getDispatchedEnergySummaryResult.sendingRate);
                    break;
                case 2:  cell.setCellValue(getDispatchedEnergySummaryResult.numberOfSuccessfulTransactions);
                    break;
                case 3:  cell.setCellValue(getDispatchedEnergySummaryResult.numberOfFailedTransactions);
                    break;
                case 4:  cell.setCellValue(getDispatchedEnergySummaryResult.overallLatency[0]);
                    break;
                case 5:  cell.setCellValue(getDispatchedEnergySummaryResult.overallLatency[1]);
                    break;
                case 6:  cell.setCellValue(getDispatchedEnergySummaryResult.overallLatency[2]);
                    break;
                case 7:  cell.setCellValue(getDispatchedEnergySummaryResult.overallThroughPut);
                    break;
                default: cell.setCellValue("-");
                    break;
            }
        }


        // Write Get Balance Round Result
        for(RoundResult rs: getBalanceRoundResults){//This assumes that the round results are arranged in order inside biddingRoundResults arraylist. i.e the first element is round 0
            Row row= getBalanceSheet.createRow(rs.round+1); //The first line should come on the second line as the header appears on the first
            for(int col=0;col<columns.length;col++){
                Cell cell = row.createCell(col);
                switch(col) {
                    case 0: cell.setCellValue(rs.round);
                        break;
                    case 1: cell.setCellValue(rs.sendingRate);
                        break;
                    case 2:  cell.setCellValue(rs.numberOfSuccessfulTransactions);
                        break;
                    case 3:  cell.setCellValue(rs.numberOfFailedTransactions);
                        break;
                    case 4:  cell.setCellValue(rs.roundLatency[0]);
                        break;
                    case 5:  cell.setCellValue(rs.roundLatency[1]);
                        break;
                    case 6:  cell.setCellValue(rs.roundLatency[2]);
                        break;
                    case 7:  cell.setCellValue(rs.roundThroughput);
                        break;
                    case 8:  cell.setCellValue(rs.lastSubmissionTimeMinusFirstSubmissionTime);
                        break;
                    case 9:  cell.setCellValue(rs.lastConfirmationTimeMinusFirstSubmissionTime);
                        break;
                    default: cell.setCellValue("-");
                        break;
                }
            }
        }

        //Write Get Balance Summary Result
        Row getBalanceSummaryRow=getBalanceSheet.createRow(getBalanceRoundResults.size()+1);
        for(int col=0;col<columns.length;col++){
            Cell cell = getBalanceSummaryRow.createCell(col);
            switch(col) {
                case 0: cell.setCellValue("Summary");
                    break;
                case 1: cell.setCellValue(getBalanceSummaryResult.sendingRate);
                    break;
                case 2:  cell.setCellValue(getBalanceSummaryResult.numberOfSuccessfulTransactions);
                    break;
                case 3:  cell.setCellValue(getBalanceSummaryResult.numberOfFailedTransactions);
                    break;
                case 4:  cell.setCellValue(getBalanceSummaryResult.overallLatency[0]);
                    break;
                case 5:  cell.setCellValue(getBalanceSummaryResult.overallLatency[1]);
                    break;
                case 6:  cell.setCellValue(getBalanceSummaryResult.overallLatency[2]);
                    break;
                case 7:  cell.setCellValue(getBalanceSummaryResult.overallThroughPut);
                    break;
                default: cell.setCellValue("-");
                    break;
            }
        }


        //Writing Market Clearance Result
        //We don't print throughput for market clearance as it doesn't make sense to calculate throughput for market clearance that has only one transaction.
           for(RoundResult rs: marketClearanceRoundResults){ //This assumes that the round results are arranged in order inside marketClearanceRoundResults arraylist
                Row row= marketClearanceSheet.createRow(rs.round+1); //The first line should come on the second line as the header appears on the first
                for(int col=0;col<columns.length;col++){
                    Cell cell = row.createCell(col);
                    switch(col) {
                        case 0: cell.setCellValue(rs.round);
                            break;
                        case 1: cell.setCellValue(rs.sendingRate);
                            break;
                        case 2:  cell.setCellValue(rs.numberOfSuccessfulTransactions);
                            break;
                        case 3:  cell.setCellValue(rs.numberOfFailedTransactions);
                            break;
                        case 4:  cell.setCellValue(rs.roundLatency[0]);
                            break;
                        case 5:  cell.setCellValue(rs.roundLatency[1]);
                            break;
                        case 6:  cell.setCellValue(rs.roundLatency[2]);
                            break;
                        case 7:  cell.setCellValue(rs.roundThroughput);
                            break;
                        case 8:  cell.setCellValue(rs.averageGasUsed);
                            break;
                        default: cell.setCellValue("-");
                            break;
                    }
                }
        }

        //Write Market Clearance Summary Result
        //We don't print throughput for market clearance as it doesn't make sense to calculate throughput for market clearance that has only one transaction.
        Row marketClearanceSummaryRow=marketClearanceSheet.createRow(marketClearanceRoundResults.size()+1);
        for(int col=0;col<columns.length;col++){
            Cell cell = marketClearanceSummaryRow.createCell(col);
            switch(col) {
                case 0: cell.setCellValue("Summary");
                    break;
                case 1: cell.setCellValue(marketClearanceSummaryResult.sendingRate);
                    break;
                case 2:  cell.setCellValue(marketClearanceSummaryResult.numberOfSuccessfulTransactions);
                    break;
                case 3:  cell.setCellValue(marketClearanceSummaryResult.numberOfFailedTransactions);
                    break;
                case 4:  cell.setCellValue(marketClearanceSummaryResult.overallLatency[0]);
                    break;
                case 5:  cell.setCellValue(marketClearanceSummaryResult.overallLatency[1]);
                    break;
                case 6:  cell.setCellValue(marketClearanceSummaryResult.overallLatency[2]);
                    break;
                case 7:  cell.setCellValue(marketClearanceSummaryResult.overallThroughPut);
                    break;
                case 8:  cell.setCellValue(marketClearanceSummaryResult.overAllAverageGasUsed);
                default: cell.setCellValue("-");
                    break;
            }


        }



        //Writing Payment Settlement Result
        //We don't print throughput forPayment Settlement as it doesn't make sense to calculate throughput for market clearance that has only one transaction.
        for(RoundResult rs: paymentSettlementRoundResults){ //This assumes that the round results are arranged in order inside marketClearanceRoundResults arraylist
            Row row= paymentSettlementSheet.createRow(rs.round+1); //The first line should come on the second line as the header appears on the first
            for(int col=0;col<columns.length;col++){
                Cell cell = row.createCell(col);
                switch(col) {
                    case 0: cell.setCellValue(rs.round);
                        break;
                    case 1: cell.setCellValue(rs.sendingRate);
                        break;
                    case 2:  cell.setCellValue(rs.numberOfSuccessfulTransactions);
                        break;
                    case 3:  cell.setCellValue(rs.numberOfFailedTransactions);
                        break;
                    case 4:  cell.setCellValue(rs.roundLatency[0]);
                        break;
                    case 5:  cell.setCellValue(rs.roundLatency[1]);
                        break;
                    case 6:  cell.setCellValue(rs.roundLatency[2]);
                        break;
                    case 7:  cell.setCellValue(rs.roundThroughput);
                        break;
                    case 8:  cell.setCellValue(rs.averageGasUsed);
                        break;
                    default: cell.setCellValue("-");
                        break;
                }
            }
        }

        //Write Payment Settlement Summary Result
        //We don't print throughput for Payment Settlement as it doesn't make sense to calculate throughput for market clearance that has only one transaction.
        Row paymentSettlementSummaryRow=paymentSettlementSheet.createRow(paymentSettlementRoundResults.size()+1);
        for(int col=0;col<columns.length;col++){
            Cell cell = paymentSettlementSummaryRow.createCell(col);
            switch(col) {
                case 0: cell.setCellValue("Summary");
                    break;
                case 1: cell.setCellValue(paymentSettlementSummaryResult.sendingRate);
                    break;
                case 2:  cell.setCellValue(paymentSettlementSummaryResult.numberOfSuccessfulTransactions);
                    break;
                case 3:  cell.setCellValue(paymentSettlementSummaryResult.numberOfFailedTransactions);
                    break;
                case 4:  cell.setCellValue(paymentSettlementSummaryResult.overallLatency[0]);
                    break;
                case 5:  cell.setCellValue(paymentSettlementSummaryResult.overallLatency[1]);
                    break;
                case 6:  cell.setCellValue(paymentSettlementSummaryResult.overallLatency[2]);
                    break;
                case 7:  cell.setCellValue(paymentSettlementSummaryResult.overallThroughPut);
                    break;
                default: cell.setCellValue("-");
                    break;
            }


        }

        // Write the output to a file
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream("TransactionStatistics.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void  pushQueryTransactionReport(QueryTransaction queryTransaction) {
       // System.out.println("Pushing Query Transaction");
        if(System.currentTimeMillis()<testSchedule.endTime)
            listOfALLQueryTransactions.add(queryTransaction);
    }
}
