package uf.cs.cn.message;

import uf.cs.cn.utils.BitFieldUtils;

import java.io.IOException;
import java.lang.Math.*;

public class BitfieldMessage extends ActualMessage {
    public BitfieldMessage(int peer_id, int messageLength) throws Exception {
        super(messageLength, MessageType.BIT_FIELD);
    }

    public byte[] generatePayload() throws IOException {
        int byteVal = 0;
        int i;
        int j =0;
        byte[] messageBody = new byte[getMessage_length()];

        for (i=0; i<this.getMessage_length();i++) {
            if (i!=0 && i%7 == 0) {
                messageBody[j] = (byte) byteVal;
                byteVal = 0;
                j+=1;
            }
            int exp = 7 - i%7;
            byteVal+= Math.pow(2, exp);
            
        }
        if (byteVal != 0) {
                messageBody[++j] = (byte) byteVal;
            }

        System.out.println("Message body is  " + messageBody);
        setPayload(messageBody);

        return getEncodedMessage();
        }
    }
