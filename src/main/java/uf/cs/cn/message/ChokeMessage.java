package uf.cs.cn.message;

import java.io.IOException;

public class ChokeMessage extends ActualMessage {

    public ChokeMessage() throws Exception {
        super(1, MessageType.CHOKE);
    }

    public byte[] getChokeMessageBytes() {
        try {
            return getEncodedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
