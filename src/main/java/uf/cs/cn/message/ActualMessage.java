package uf.cs.cn.message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ActualMessage {
    private int message_length;
    private byte message_type;
    private byte[] payload;

    ActualMessage(int message_length, byte message_type) throws Exception {
        this.message_length = message_length;
        this.setMessage_type(message_type);
        payload = new byte[message_length];
    }

    public void setMessage_type(byte num) throws Exception {
        if(num< 0 || num >7) {
            throw new Exception("Invalid Message Type");
        }
        this.message_type = num;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getEncodedMessage() throws IOException {
        // TODO: look for a better logic if any
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(("" + message_length).getBytes());
        outputStream.write(message_type);
        outputStream.write(this.payload);
        return  outputStream.toByteArray();
    }

}
