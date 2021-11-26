package uf.cs.cn.message;

import java.io.IOException;

public class PieceMessage extends ActualMessage{

    /**
     * This constructor will mainly be used when we need to send a piece to other nodes.
     * @param piece_id the file piece id
     * @param file_chunk
     * @throws Exception
     */
    public PieceMessage(int piece_id,byte[] file_chunk) throws Exception {
        // file chunk and 1 byte for the type of the message
        super(file_chunk.length+1,MessageType.PIECE);
        setPayload(file_chunk);
    }

    public PieceMessage(byte[] message_length, byte[] payload) {
        super(message_length,payload);
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
