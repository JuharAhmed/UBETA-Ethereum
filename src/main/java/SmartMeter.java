import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class SmartMeter implements Runnable {
    public int smartMeterID;
    Constants.Role role;
    public static Web3j web3j = null;
    Credentials credential;
    TestSchedule testSchedule;
    ArrayList<Task> myRoundTasks;
    ArrayList<String> urls;
    ArrayList<String> privateKeys;
    ArrayList <EnergyData>  energyDataList;

    SmartMeter(Constants.Role role, ArrayList<String> urls, ArrayList<String> privateKeys, TestSchedule testSchedule){
        this.role=role;
        this.urls=urls;
        this.privateKeys=privateKeys;
        this.testSchedule=testSchedule;
    }

    @Override
    public void  run() {

        int transactionCounter;
        int numberOfRounds=testSchedule.numberOfRounds;
        Task task;
        long timeGapBetweenSchedulingAndSending=5000;
        long waitingTime=0;
        long sendingTime;

        if (this.role==  Constants.Role.CONSUMER){

            System.out.println("Consumer Main Thread started");
            for(int round=0; round <numberOfRounds;round++){

                waitingTime=testSchedule.testRounds.get(round).roundStartTime - System.currentTimeMillis();
                try {
                    if(waitingTime >1000)
                        Thread.sleep(waitingTime); //  Timers are scheduled just when 50 milli seconds is left for the transaction to be sent
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myRoundTasks=getSmartMeterTasks(round);
                //Scheduling tasks for the round
                for(int ts=0;ts<myRoundTasks.size();ts++){
                   task=myRoundTasks.get(ts);

                  if(task.taskType==Constants.TransactionType.BiM_MultiSigContract){

                   }
                  else if(task.taskType==Constants.TransactionType.PM_Initialization){

                  }
                 else if(task.taskType==Constants.TransactionType.PM_Bidding){
                      readPMBidData(task.numberOfTransactionsPerClient);
                   //Scheduling Worker Threads ahead of time
                     transactionCounter=0; // Also used as the ID of the worker threads
                      System.out.println("Consumer Main Thread: Round: "+round+" Scheduling Worker Threads to send Bids at a rate of: " +task.transactionRatePerClient+
                              " TPS for a duration of "+task.duration/1000+" seconds");
                    while(transactionCounter<task.numberOfTransactionsPerClient){
                         credential = Credentials.create(privateKeys.get(transactionCounter));
                         sendingTime=task.startTime +transactionCounter*(1000/task.transactionRatePerClient);
                          BidSender bidSender= new BidSender( sendingTime,this, credential, urls, round, transactionCounter, energyDataList.get(transactionCounter).energyAmount, energyDataList.get(transactionCounter).price);
                          Thread thread= new Thread(bidSender);
                          thread.start();
                          transactionCounter++;
                      }

                  }

                  else if(task.taskType==Constants.TransactionType.PM_getDispachedEnergy){


                  }
                  else if(task.taskType==Constants.TransactionType.PS_getBalance){
                       transactionCounter=0; // Also used as the ID of the worker threads
                      System.out.println("Consumer Main Thread: Round: "+round+" Scheduling Worker Threads to Query balance at a rate of: " +task.transactionRatePerClient+
                              " TPS for a duration of "+task.duration/1000+" seconds");
                      while(transactionCounter<task.numberOfTransactionsPerClient){
                          credential = Credentials.create(privateKeys.get(transactionCounter));
                          sendingTime=task.startTime +transactionCounter*(1000/task.transactionRatePerClient);
                          QuerySenderGetBalance querySenderGetBalance = new QuerySenderGetBalance(sendingTime,this, credential, urls, round, transactionCounter);
                          Thread thread= new Thread(querySenderGetBalance);
                          thread.start();
                          transactionCounter++;
                      }

                  }

                  else if(task.taskType==Constants.TransactionType.BaM_Offers){

                  }
                  else if(task.taskType==Constants.TransactionType.PS_ConsumptionProduction){

                  }


                }

                System.out.println("Consumer Main Thread Round "+round+" Scheduling is Finished");

            }

        }
        else if (this.role ==  Constants.Role.PRODUCER){

            System.out.println("Producer Main Thread started");

            for(int round=0; round <numberOfRounds;round++){

                waitingTime=testSchedule.testRounds.get(round).roundStartTime - System.currentTimeMillis();
                try {
                    if(waitingTime >1000)
                        Thread.sleep(waitingTime); //  Timers are scheduled just when 50 milli seconds is left for the transaction to be sent
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myRoundTasks=getSmartMeterTasks(round);
                //Scheduling tasks for the round
                for(int ts=0;ts<myRoundTasks.size();ts++){
                    task=myRoundTasks.get(ts);

                    if(task.taskType==Constants.TransactionType.BiM_MultiSigContract){

                    }
                    else if(task.taskType==Constants.TransactionType.PM_Initialization){

                    }
                    else if(task.taskType==Constants.TransactionType.PM_Bidding){
                        readPMOfferData(task.numberOfTransactionsPerClient);
                        transactionCounter=0;
                        System.out.println("Producer Main Thread Round: "+round+" Scheduling Worker Threads to send Offers at a rate of: " +task.transactionRatePerClient+
                                " TPS for a duration of "+task.duration/1000+" seconds");
                        while(transactionCounter<task.numberOfTransactionsPerClient){
                           credential = Credentials.create(privateKeys.get(transactionCounter));
                            sendingTime=task.startTime +transactionCounter*(1000/task.transactionRatePerClient);
                            OfferSender offerSender= new OfferSender(sendingTime,this, credential, urls,round, transactionCounter, energyDataList.get(transactionCounter).energyAmount, energyDataList.get(transactionCounter).price);
                            Thread thread= new Thread(offerSender);
                            thread.start();
                            transactionCounter++;

                        }

                    }

                    else if(task.taskType==Constants.TransactionType.PM_getDispachedEnergy){


                    }
                    else if(task.taskType==Constants.TransactionType.PS_getBalance) {
                        transactionCounter = 0; // Also used as the ID of the worker threads
                        System.out.println("Producer Main Thread: Round: " + round + " Scheduling Worker Threads to Query Balance at a rate of: " + task.transactionRatePerClient +
                                " TPS for a duration of " + task.duration / 1000 + " seconds");
                        while (transactionCounter < task.numberOfTransactionsPerClient) {
                            credential = Credentials.create(privateKeys.get(transactionCounter));
                            sendingTime=task.startTime +transactionCounter*(1000/task.transactionRatePerClient);
                            QuerySenderGetBalance querySenderGetBalance = new QuerySenderGetBalance(sendingTime,this, credential, urls, round, transactionCounter);
                            Thread thread= new Thread(querySenderGetBalance);
                            thread.start();
                            transactionCounter++;
                        }
                    }

                    else if(task.taskType==Constants.TransactionType.BaM_Offers){

                    }
                    else if(task.taskType==Constants.TransactionType.PS_ConsumptionProduction){

                    }

                }

                System.out.println("Producer Main Thread Round "+round+" Scheduling is Finished");
            }

        }

        // Waiting until worker Threads finish
        try {
           Thread.sleep(testSchedule.endTime-System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private ArrayList<Task> getSmartMeterTasks(int round) {
        ArrayList<Task> roundTasks;
        ArrayList<Task> smartMeterTasks=new ArrayList<>();
        System.out.println("Smart Meter Round "+round+" Taks: ");
        roundTasks=testSchedule.testRounds.get(round).tasks;
        for(Task ts:roundTasks){
            if(ts.taskType==Constants.TransactionType.BiM_MultiSigContract || ts.taskType==Constants.TransactionType.PM_Bidding || ts.taskType==Constants.TransactionType.PM_getDispachedEnergy || ts.taskType==Constants.TransactionType.PS_getBalance || ts.taskType==Constants.TransactionType.BaM_Offers || ts.taskType==Constants.TransactionType.PS_ConsumptionProduction){
                smartMeterTasks.add(ts);
                System.out.println("Task Type: "+ts.taskType);
            }
        }
        return smartMeterTasks;
    }

    public void readPMBidData(long numberOfTransactions) {
        energyDataList=new ArrayList<>();
        System.out.println("Reading Bidding Data from File");
        //File path to energy bid data
        String powerDataFile = "C:/Users/s3753266/RMIT-C/web3j/PowerData/Western Australia/WesAus_PoolMarketBidData_SmallerSize.xlsx"; // This is only for PM_Bidding
       // String powerDataFile = "C:/Users/s3753266/RMIT-C/web3j/PoolMarketBidData.xlsx";
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(powerDataFile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);
        long numberOfRowsToRead=0 ;
        for (Row row: sheet) {
            //We read one more row as the first row starts from 1 instead of 0
            if (numberOfRowsToRead > numberOfTransactions) { // The data should not be less than the number of transactions in each round
                break;
            }
            if (numberOfRowsToRead == 0) {
                //Don't read anything as this the header row
            } else {
                EnergyData energyData = new EnergyData();
                energyData.energyAmount = BigInteger.valueOf((long) row.getCell(5).getNumericCellValue());
                energyData.price =  BigInteger.valueOf(Math.abs((long) row.getCell(6).getNumericCellValue()));
                //  System.out.println("Energy Bid: Amount: "+energyData.energyAmount+" Price: "+energyData.price);
            energyDataList.add(energyData);
           // System.out.println("Consumer Main Thread: " + " Energy Amount: " + energyData.energyAmount + " Energy price: " + energyData.price);
        }
           numberOfRowsToRead++;
        }
        System.out.println("Reading Bidding Data Finished");
    }

    private void readPMOfferData(long numberOfTransactions) {
        energyDataList=new ArrayList<>();
        System.out.println("Reading Offer Data From File");
        //File path to energy Offer data
      String powerDataFile = "C:/Users/s3753266/RMIT-C/web3j/PowerData/Western Australia/WesAus_PoolMarketOfferData_SmallerSize.xlsx"; // This is only for PM_Bidding
      //  String powerDataFile = "C:/Users/s3753266/RMIT-C/web3j/PoolMarketOfferData.xlsx";
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(powerDataFile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);
        long numberOfRowsToRead=0;
        for (Row row: sheet) {
            //We read one more row as the first row starts from 1 instead of 0
            if (numberOfRowsToRead > numberOfTransactions) { // The data should not be less than thenumber of transactions in each round
                break;
            }
            if (numberOfRowsToRead == 0) {
                //Don't read anything as this the header row
            } else {
                EnergyData energyData = new EnergyData();
                energyData.energyAmount = BigInteger.valueOf((long) row.getCell(5).getNumericCellValue());
                energyData.price = BigInteger.valueOf(Math.abs((long) row.getCell(6).getNumericCellValue()));
                //  System.out.println("Energy Bid: Amount: "+energyData.energyAmount+" Price: "+energyData.price);
                energyDataList.add(energyData);
              //  System.out.println("Producer Main Thread: " + " Energy Amount: " + energyData.energyAmount + " Energy price: " + energyData.price);
            }
            numberOfRowsToRead++;
        }
    }


}
