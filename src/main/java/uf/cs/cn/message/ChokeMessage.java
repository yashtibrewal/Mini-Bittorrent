package uf.cs.cn.message;

public class ChokeMessage extends ActualMessage {

    public ChokeMessage() throws Exception {
        super(1, MessageType.CHOKE);
    }
}
