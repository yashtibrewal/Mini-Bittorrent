package uf.cs.cn.message;

import uf.cs.cn.peer.Peer;
import uf.cs.cn.utils.BitFieldUtils;
import uf.cs.cn.utils.PeerInfoConfigFileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BitfieldMessage extends ActualMessage {
    public BitfieldMessage(int messageLength) throws Exception {
        super(messageLength + 1, MessageType.BIT_FIELD);
    }

    public byte[] generatePayload() throws IOException {
        byte byteVal = 0;
        int i;
        boolean isServer = PeerInfoConfigFileReader.isPeerServer(Peer.getPeerId());

        ArrayList<Byte> messageBody = new ArrayList<>();
        for (i = 0; i < BitFieldUtils.getNumberOfChunks(); i++) {
            if (i != 0 && i % 8 == 0) {

                if (!isServer) {
                    messageBody.add((byte) 0);
                } else {
                    messageBody.add(byteVal);
                }
                byteVal = 0;
            }
            int exp = 7 - i % 8;
            byteVal += Math.pow(2, exp);

        }
        if (byteVal != 0) {

            if (!isServer) {
                messageBody.add((byte) 0);
            } else {
                messageBody.add(byteVal);
            }
        }

        byte[] result = new byte[messageBody.size()];
        for (i = 0; i < messageBody.size(); i++) {
            result[i] = messageBody.get(i);
        }

        setPayload(result);
        return getEncodedMessage();
    }
}
