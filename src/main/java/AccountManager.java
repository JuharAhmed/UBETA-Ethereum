import org.web3j.crypto.*;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
     static ArrayList<Account>  accounts= new ArrayList<>();
    static ArrayList<String>  privateKeys= new ArrayList<>();
    static ArrayList<String>  addresses= new ArrayList<>();

    public static ArrayList<String> getPrivateKeys(int numberOfUserAccounts){
        AccountManager.readPrivateKeysFromFile(numberOfUserAccounts);
return privateKeys;
    }

    public static List<String> getAddresses (int numberOfUserAccounts){
        AccountManager.readAddressesFromFile(numberOfUserAccounts);
        return addresses;
    }

    public static void createAccounts(int numberOfAccounts){
        ECKeyPair ecKeyPair = null;
        String address;
        String privateKey;
        for(int i=0;i<numberOfAccounts;i++){
            try {
                ecKeyPair = Keys.createEcKeyPair();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            privateKey = ecKeyPair.getPrivateKey().toString(16);
            System.out.println(" Private Key : " + privateKey);
            address = Keys.getAddress(ecKeyPair);
            System.out.println(" Address : " + address);

            Account account = new Account(address, privateKey, "90000000000000000000000000");
            accounts.add(account);
        }
    }

    public static void readPrivateKeysFromFile(int numberOfUserAccounts) {

        FileReader reader = null;
        try {
            reader = new FileReader("PrivateKeys_20000.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;
      int counter=0;
        while (counter < numberOfUserAccounts) {
            try {
                if (!((line = bufferedReader.readLine()) != null)) break;
                privateKeys.add(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Credentials createCredentials(String privateKey){ // This anly works if we know the private key
        Credentials credentials = Credentials.create(privateKey);
        return credentials;
    }

    public static Credentials createCredentials(String password, WalletFile wallet){
        Credentials credentials = null;
        try {
            credentials = Credentials.create(Wallet.decrypt(password, wallet));
        } catch (CipherException e) {
            e.printStackTrace();
        }
        String privateKey= credentials.getEcKeyPair().getPrivateKey().toString(16);
        System.out.println("Password+ "+password+ " Private Key : "+privateKey);
        System.out.println("Address+ "+credentials.getAddress());
        return credentials;
    }


    public  static WalletFile createWallet (String seed){ // The seed is user password used to protect the wallet
        WalletFile wallet = null;
        ECKeyPair ecKeyPair = null;
        try {
            ecKeyPair = Keys.createEcKeyPair();
            String privateKey = ecKeyPair.getPrivateKey().toString(16);
           System.out.println(" Seed " +seed+ " Private Key : "+privateKey);

            try {
                wallet = Wallet.createStandard(seed, ecKeyPair);
            } catch (CipherException e) {
                e.printStackTrace();
            }
            System.out.println("Seed " +seed+ " Address : "+ wallet.getAddress());

        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return wallet;
    }

    public static void writeAccountsToFile() {

        FileWriter writer = null;
        try {
            writer = new FileWriter("Accounts.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        for(Account account: accounts){
            try {
                bufferedWriter.write("  \""+ account.address+"\": "+"{");
                bufferedWriter.newLine();
                bufferedWriter.write("      \"privateKey\": "+"\""+ account.privateKey+"\",");
                bufferedWriter.newLine();
                bufferedWriter.write("      \"balance\": "+"\""+ account.balance+"\"");
                bufferedWriter.newLine();
                bufferedWriter.write("  },");
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePrivateKeysToFile() { // I am writing private keys separately because i use them to cerate credentials for smart meters

        FileWriter writer = null;
        try {
            writer = new FileWriter("PrivateKeys.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        for(Account account: accounts){
            try {
                bufferedWriter.write(account.privateKey);
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void writeAddressesToFile() { // I am writing private keys separately because i use them to cerate credentials for smart meters

        FileWriter writer = null;
        try {
            writer = new FileWriter("Addresses.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        for(Account account: accounts){
            try {
                bufferedWriter.write(account.address);
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void readAddressesFromFile(int numberOfUserAccounts) {
        FileReader reader = null;
        try {
            reader = new FileReader("Addresses_20000.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;
        int counter=0;
        while (counter <numberOfUserAccounts) {
            try {
                if (!((line = bufferedReader.readLine()) != null)) break;
                addresses.add(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeSolidityAddressesToFile(List<String> addresses) { // I am writing private keys separately because i use them to cerate credentials for smart meters

        FileWriter writer = null;
        try {
            writer = new FileWriter("SolidityAddresses.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        for(String address: addresses){
            try {

                bufferedWriter.write("_balanceOf["+ Keys.toChecksumAddress(address)+"]=1000000;");
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
