package uf.cs.cn.message;

import uf.cs.cn.utils.BitFieldUtils;

import java.io.IOException;
import java.lang.Math.*;
import java.util.Arrays;

public class BitfieldMessage extends ActualMessage {
    public BitfieldMessage(int peer_id, int messageLength) throws Exception {
        super(messageLength, MessageType.BIT_FIELD);
    }

    public byte[] generatePayload() throws IOException {
        int byteVal = 0;
        int i;
        int j =0;

//
//        if (numChunks%8 == 0) {
//            messageLength = numChunks/8;
//        } else {
//            messageLength = numChunks/8 + 1;
//        }

        byte[] messageBody = new byte[getMessage_length()];

        for (i=0; i<this.getMessage_length();i++) {
            if (i!=0 && i%7 == 0) {
                messageBody[j] = (byte) byteVal;
                System.out.print("The message body is " + messageBody[j]);
                byteVal = 0;
                j+=1;
            }
            int exp = 7 - i%7;
            byteVal+= Math.pow(2, exp);
            
        }
        if (byteVal != 0) {
            messageBody[++j] = (byte) byteVal;
            System.out.print(Integer.toBinaryString(byteVal));
            System.out.println();
            }

        System.out.println("Message body is " + (Arrays.toString(messageBody)));
        setPayload(messageBody);

        return getEncodedMessage();
        }
    }
