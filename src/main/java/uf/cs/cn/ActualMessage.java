package uf.cs.cn;
import java.lang.instrument.Instrumentation;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ActualMessage {


    int message_length;
    byte message_type;
    ArrayList<Character> message_payload;

    ActualMessage() {
        message_length = 100;
        message_type = 1;
        message_payload = new ArrayList<>(message_length);
    }

    public void setMessage_length(int message_length) {
        this.message_length = message_length;
    }

}
