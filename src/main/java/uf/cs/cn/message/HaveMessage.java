package uf.cs.cn.message;

import uf.cs.cn.utils.ActualMessageUtils;

import java.util.Arrays;

public class HaveMessage extends ActualMessage {

    int piece_index;

    public HaveMessage(int piece_index) throws Exception {
        this.piece_index = piece_index;
        setMessage_type(MessageType.HAVE);
        setMessageLength(1 + 4); // type and piece index
        setPayload(ActualMessageUtils.convertIntToByteArray(piece_index));
    }

    /**
     * No need to send the message_length
     *
     * @param payload
     */
    public HaveMessage(byte[] payload) {
        this.piece_index = convertByteArrayToInt(Arrays.copyOfRange(payload, 1, 5));
    }

    public int getPieceIndex() {
        return this.piece_index;
    }
}
