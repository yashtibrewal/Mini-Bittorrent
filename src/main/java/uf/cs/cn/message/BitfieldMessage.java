package uf.cs.cn.message;

import uf.cs.cn.utils.BitFieldUtils;

import java.io.IOException;
import java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BitfieldMessage extends ActualMessage {
    public BitfieldMessage(int messageLength, int peer_id) throws Exception {
        super(messageLength, MessageType.BIT_FIELD);
    }


    public byte[] generatePayload() throws IOException {
        int byteVal = 0;
        int i;
        int j =0;

        ArrayList<Byte> messageBody = new ArrayList<Byte>();
        for (i=0; i<this.getMessage_length();i++) {
            if (i!=0 && i%8 == 0) {
                messageBody.add((byte)byteVal);
                byteVal = 0;
                j+=1;
            }
            int exp = 7 - i%8;
            byteVal+= Math.pow(2, exp);
            
        }
        if (byteVal != 0) {
            messageBody.add((byte)byteVal);
            System.out.println();
            }

        byte[] result = new byte[messageBody.size()];
        for(i = 0; i < messageBody.size(); i++) {
            result[i] = messageBody.get(i).byteValue();
        }

        setPayload(result);
        return getEncodedMessage();
        }
    }
