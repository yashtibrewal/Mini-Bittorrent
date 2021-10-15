package uf.cs.cn;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HandShakeMessage implements Serializable {

    private char[] header;
    private char[] zero_bits;
    private char[] peer_id; // 4 bytes

    HandShakeMessage() {
        // 18 bytes
        header = new char[]{'P','2','P','F','I','L','E','S','H','A','R','I','N','G','P','R','O','J'};
        // 10 bytes
        zero_bits = new char[]{'0','0','0','0','0','0','0','0','0','0'};
    }

    public void setPeer_id(char[] peer_id) {
        this.peer_id = peer_id;
    }

    public char[] getPeer_id() {
        return peer_id;
    }

    /**
     *
     * @return the character stream
     */
    String getMessage() {
        return new String(header) + new String(zero_bits) + new String(peer_id);
    };

}
