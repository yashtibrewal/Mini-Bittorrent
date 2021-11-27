package uf.cs.cn.message;

import uf.cs.cn.peer.Peer;
import uf.cs.cn.utils.PeerLogging;

public class HandShakeMessage {

    private byte[] header;
    private byte[] zero_bits;
    private byte[] peer_id; // 4 bytes
    private int id;
    private PeerLogging peerLogging;

    // Byte implementation, takes in 32 byte buffer
    public HandShakeMessage(byte[] buffer)throws Exception {
        peerLogging = new PeerLogging();
        if(buffer.length != 32){
            peerLogging.genericErrorLog("Invalid Length of Buffer");
        }
        header = new byte[18];
        zero_bits = new byte[10];
        peer_id = new byte[4];
        for(int i=0;i<18;i++) {
            header[i] = buffer[i];
        }
        int index = 0;
        for(int i=18;i<28;i++) {
            zero_bits[index] = buffer[i];
            index++;
        }
        index = 0;
        for(int i=28;i<32;i++) {
            peer_id[index] = buffer[i];
            index++;
        }
        id = 0;
        for(int i=0;i<4;i++) {
            id = id*10+(peer_id[i]-48);
        }
    }

    public HandShakeMessage(int peer_id_param) {
        // 18 bytes
        header = new byte[]{'P','2','P','F','I','L','E','S','H','A','R','I','N','G','P','R','O','J'};
        // 10 bytes
        zero_bits = new byte[]{'0','0','0','0','0','0','0','0','0','0'};
        peer_id = ("" + peer_id_param).getBytes();
        this.id = peer_id_param;
    }

    public String getMessage() {
        return new String(header) + new String(zero_bits) + this.id;
    }

    public int getPeerId() {
        return id;
    }

    public boolean checkPeerId(int expected_peer_id) {
        return this.getPeerId() == expected_peer_id;
    }

    public byte[] getBytes() {
        return this.getMessage().getBytes();
    }

}
