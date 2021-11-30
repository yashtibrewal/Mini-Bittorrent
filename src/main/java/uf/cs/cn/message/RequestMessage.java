package uf.cs.cn.message;
import uf.cs.cn.utils.ActualMessageUtils;

public class RequestMessage extends ActualMessage {
    private int pieceIndex;

    public RequestMessage(int pieceIndex) throws Exception {
        super(4+1, MessageType.REQUEST);
        this.pieceIndex = pieceIndex;
        byte[] payload = new byte[5];
        payload[0] = MessageType.REQUEST;
        System.arraycopy(ActualMessageUtils.convertIntToByteArray(pieceIndex),0,payload,1,4);
        setPayload(payload);
    }
}
