package uf.cs.cn.message;

import uf.cs.cn.utils.BitFieldUtils;
import java.lang.Math.*;

public class BitfieldMessage extends ActualMessage {
    int peer_id;

    public BitfieldMessage(int message_length, byte message_type) throws Exception {
        super(message_length, message_type);
        int numChunks = BitFieldUtils.getNumberOfChunks(this.peer_id);
    }


    public byte generatePayload() {
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

        return (byte) byteVal;
        }
    }
