package uf.cs.cn.message;

import java.io.IOException;

public class ChokeMessage extends ActualMessage{

    ChokeMessage() throws Exception {
        super(4, (byte) 0);
    }

    public byte[] getChokeMessageBytes(){
        try {
            return getEncodedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
