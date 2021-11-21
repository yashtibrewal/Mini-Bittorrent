package uf.cs.cn.message;

import java.io.IOException;

public class UnchokeMessage extends ActualMessage{

    UnchokeMessage() throws Exception {
        super(4, (byte) 1);
    }

    public byte[] getUnchokeMessageBytes(){
        try {
            return getEncodedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
