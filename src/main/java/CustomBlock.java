import org.web3j.protocol.core.methods.response.EthBlock;

public class CustomBlock {
    EthBlock.Block block;
    long timeReceived;
    public CustomBlock(EthBlock.Block block, long timeReceived){
    this.block=block;
    this.timeReceived=timeReceived;
}
}
