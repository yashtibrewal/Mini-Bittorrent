package uf.cs.cn.utils;

import java.util.ArrayList;

/*
    This class is used for providing methods to generate BitField messages

 */

public class BitFieldUtils {

    public static int isDivsible(int a, int b) {
        return (a % b == 0 ? a / b : (a / b) + 1);
    }

    public static int getNumberOfChunks() {
        int size = isDivsible(CommonConfigFileReader.file_size, CommonConfigFileReader.piece_size);
        return size;
    }

    public static int getPayloadDataSize(int numChunks) {
        return isDivsible(numChunks, 8);
    }

    public static ArrayList<Boolean> convertToBoolArray(byte[] payload) {
        int i;
        int j;
        ArrayList<Boolean> response = new ArrayList<Boolean>();

        String[] binaries = new String[payload.length];

        int numChunks = getNumberOfChunks();
        int ctr = 0;
        for (i = 0; i < payload.length; i++) {
            binaries[i] = Integer.toBinaryString(payload[i] & 0xff);
        }

        for (i = 0; i < binaries.length; i++) {
            for (j = 0; j < binaries[i].length(); j++) {
                if (ctr == numChunks) {
                    break;
                }
                response.add(binaries[i].charAt(j) == '1');
                ctr++;
            }
        }
        return response;
    }

}

