package uf.cs.cn.message;

import java.io.IOException;

public class UnChokeMessage extends ActualMessage{

    public UnChokeMessage() throws Exception {
        super(1, MessageType.UN_CHOKE);
    }

    public byte[] getUnChokeMessageBytes(){
        try {

            return getEncodedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
