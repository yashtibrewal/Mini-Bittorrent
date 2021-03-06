package uf.cs.cn.message;

import uf.cs.cn.utils.ActualMessageUtils;
import uf.cs.cn.utils.PeerLogging;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class ActualMessage {
    /**
     * Size: 4 bytes.
     * Contains the value of the number of bytes of the payload, for example, if your message length is 5 bytes,
     * a stream reader will expect 5 bytes of data apart from the message_length's 4 bytes.
     */
    private int message_length;
    /**
     * Size: 1 byte.
     * Represents the type of message being sent, refer Message_Type enum.
     */
    private byte message_type;
    /**
     * Size: variable, the value of this size is given in message_length.
     */
    private byte[] payload;

    private PeerLogging peerLogging;

    public ActualMessage() {
    }

    public ActualMessage(int message_length, byte message_type) throws Exception {
        this.message_length = message_length;
        this.setMessage_type(message_type);
        this.payload = new byte[0];
        peerLogging = PeerLogging.getInstance();
    }

    public ActualMessage(byte[] message_length, byte[] payload) {
        this.message_length = this.convertByteArrayToInt(message_length);
        this.message_type = payload[0];
        setPayload(Arrays.copyOfRange(payload, 1, this.message_length));
        if(payload.length == 1)
        setPayload(new byte[]{});
    }

    public int convertByteArrayToInt(byte[] int_chunk) {
        return new BigInteger(int_chunk).intValue();
    }

    public void setMessageLength(int message_length) {
        this.message_length = message_length;
    }

    public int getMessage_type() {
        return this.message_type;
    }

    public void setMessage_type(byte num) throws Exception {
        if (num < 0 || num > 7) {
            peerLogging.genericErrorLog("Invalid Message Type");
        }
        this.message_type = num;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getEncodedMessage() throws IOException {
        byte[] message = new byte[4 + 1 + payload.length];
        System.arraycopy(ActualMessageUtils.convertIntToByteArray(this.message_length), 0, message, 0, 4);
        message[4] = message_type;
        System.arraycopy(payload, 0, message, 5, payload.length);
        return message;
    }
}
