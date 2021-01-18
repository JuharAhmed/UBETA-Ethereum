import java.math.BigInteger;
import java.util.ArrayList;

public class EnergyData {
    int roundNo;
    int smartMeterID;
    Constants.TransactionType taskType;
    ArrayList<BigInteger []> energyPricePairList= new ArrayList<>();
    BigInteger energyAmount=BigInteger.ZERO;
    BigInteger price=BigInteger.ZERO;
}
