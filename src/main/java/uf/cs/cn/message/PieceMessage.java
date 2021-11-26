package uf.cs.cn.message;

import uf.cs.cn.peer.Peer;
import uf.cs.cn.utils.ActualMessageUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class PieceMessage extends ActualMessage{

    private int piece_index;

    public int getPieceIndex() {
        return this.piece_index;
    }
    // TODO: Testing
    /**
     * Will create a piece message and load the payload from disk
     */
    public PieceMessage(int piece_id) throws Exception {
        this.piece_index = piece_id;
        setMessage_type(MessageType.PIECE);
        // read the bytes of that piece id
        byte[] file_chunk;
        byte[] piece_index;
        byte[] payload = new byte[0];
        try(
                FileInputStream fileInputStream = new FileInputStream(
                        Paths.get(System.getProperty("user.dir"), Peer.getPeerId()+"","piece_1").toString())
                ) {
            file_chunk = fileInputStream.readAllBytes();
            System.out.println("File size " + file_chunk.length);
            piece_index = ActualMessageUtils.convertIntToByteArray(piece_id);
            payload = new byte[4+file_chunk.length];
            System.arraycopy(piece_index, 0, payload, 0, 4);
            System.arraycopy(file_chunk, 0, payload, 4, file_chunk.length);
            this.setPayload(payload);

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setMessageLength(1+payload.length);
    }

    public PieceMessage(byte[] message_length, byte[] payload) {
        super(message_length,payload);
        this.piece_index = convertByteArrayToInt(Arrays.copyOfRange(payload,0,4));
    }

    public byte[] getFileChunk(){
        return Arrays.copyOfRange(getPayload(),4,getPayload().length);
    }
//    public static void main(String[] args) throws Exception {
//        Peer peer = Peer.getInstance(1001);
//        PieceMessage pieceMessage = new PieceMessage(1);
//        System.out.println(pieceMessage.getPayload().length);
//        System.out.println(pieceMessage.getEncodedMessage().length);
//        System.out.println(Arrays.toString(new PieceMessage(Arrays.copyOfRange(pieceMessage.getEncodedMessage(), 0, 4),
//                Arrays.copyOfRange(pieceMessage.getEncodedMessage(), 4, pieceMessage.getEncodedMessage().length)
//        ).getEncodedMessage()));
//    }
}
