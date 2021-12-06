package uf.cs.cn.utils;

import java.math.BigInteger;

public class ActualMessageUtils {

    /**
     * @param present:  represents the present byte array, e.g. [1,2,3]
     * @param pad_size: represent the number of 0 needed to be padded in the start of the array, e.g. 1
     * @return returns pad_size number of zeroes padded before the byte array. i.e [0,1,2,3]
     */
    public static byte[] padZeros(byte[] present, int pad_size) {
        byte[] result = new byte[present.length + pad_size];
        for (int i = 0; i < pad_size; i++) {
            result[i] = 0;
        }
        System.arraycopy(present, 0, result, pad_size, present.length);
        return result;
    }

    public static byte[] convertIntToByteArray(int n) {
        byte[] result = new BigInteger(n + "").toByteArray();
        if (result.length == 4) {
            return result;
        }
        result = ActualMessageUtils.padZeros(result, 4 - result.length);
        return result;
    }
}
