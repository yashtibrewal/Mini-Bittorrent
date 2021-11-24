package uf.cs.cn.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RequestMessage extends ActualMessage{
    private int pieceIndex;

    RequestMessage(int pieceIndex) throws Exception {
        super(4, MessageType.REQUEST);
        this.pieceIndex = pieceIndex;
        makeRequestMessage();
    }

    public void makeRequestMessage() {
        try {
            setPayload(ByteBuffer.allocate(4).putInt(pieceIndex).array());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getRequestMessageBytes(){
        try {
            return getEncodedMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        RequestMessage rm = new RequestMessage(9);
        System.out.println(new String(rm.getRequestMessageBytes()));
    }
}
