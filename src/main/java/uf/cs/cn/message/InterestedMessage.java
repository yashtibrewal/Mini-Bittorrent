package uf.cs.cn.message;

import java.io.IOException;

public class InterestedMessage extends ActualMessage{

    public InterestedMessage() throws Exception {
        super(1, MessageType.INTERESTED);
    }

    public byte[] getInterestedMessageBytes(){
        try {
            return getEncodedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
