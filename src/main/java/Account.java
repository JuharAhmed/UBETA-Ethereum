public class Account {
    String privateKey;
    String address;
    String balance; // Balance should number but since i am writing this to genesis file in the form of string, i use string
    Account ( String address, String privateKey, String balance){
        this.address=address;
        this.privateKey=privateKey;
        this.balance=balance;
    }
}
