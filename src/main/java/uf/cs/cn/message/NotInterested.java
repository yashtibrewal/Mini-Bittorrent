package uf.cs.cn.message;

import java.io.IOException;

public class NotInterested extends ActualMessage{

    public NotInterested() throws Exception {
        super(1, MessageType.NOT_INTERESTED);
    }

    public byte[] getNotInterestedMessageBytes(){
        try {
            return getEncodedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
