package uf.cs.cn.message;
import uf.cs.cn.utils.BitFieldUtils;

import java.io.IOException;
import java.util.ArrayList;

public class BitfieldMessage extends ActualMessage {
    public BitfieldMessage(int messageLength) throws Exception {
        super(messageLength+1, MessageType.BIT_FIELD);
    }
    public byte[] generatePayload() throws IOException {
        int byteVal = 0;
        int i;

        ArrayList<Byte> messageBody = new ArrayList<>();
        for (i=0; i< BitFieldUtils.getNumberOfChunks(); i++) {
            if (i!=0 && i%8 == 0) {
                messageBody.add((byte)byteVal);
                byteVal = 0;
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
            result[i] = messageBody.get(i);
        }

        setPayload(result);
        return getEncodedMessage();
        }
    }
