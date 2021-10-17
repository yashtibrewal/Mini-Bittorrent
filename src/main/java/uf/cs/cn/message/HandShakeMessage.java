package uf.cs.cn.message;

public class HandShakeMessage {

    private char[] header;
    private char[] zero_bits;
    private char[] peer_id; // 4 bytes

    public HandShakeMessage(int peer_id_param) {
        // 18 bytes
        header = new char[]{'P','2','P','F','I','L','E','S','H','A','R','I','N','G','P','R','O','J'};
        // 10 bytes
        zero_bits = new char[]{'0','0','0','0','0','0','0','0','0','0'};
        peer_id = ("" + peer_id_param).toCharArray();
    }

    public String getMessage() {
        return new String(header) + new String(zero_bits) + new String(peer_id);
    }

    public byte[] getEncodedMessage() {
        return this.getMessage().getBytes();
    }

}
