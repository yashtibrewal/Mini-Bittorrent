
package uf.cs.cn.utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.*;
import java.nio.file.*;

/*
    This class is used for providing methods to generate BitField messages

 */
public class BitFieldUtils {
    public static int getNumberOfChunks() throws IOException {
        int numChunks = CommonConfigFileReader.file_size/CommonConfigFileReader.piece_size;

        if (CommonConfigFileReader.file_size%CommonConfigFileReader.piece_size == 0){
            return numChunks;
        } else {
            return numChunks+1;
        }
    }

    public static ArrayList<Boolean> convertToBoolArray(byte[] payload, int peer_id) throws IOException {
        int i;
        int j;
        ArrayList<Boolean>  response = new ArrayList<Boolean>();

        String[] binaries = new String[payload.length];

        for (i=0; i < payload.length; i++) {
            binaries[i] = Integer.toBinaryString(payload[i]& 0xff);
        }

        for (i=0; i< binaries.length ; i++ ){
            for (j=0; i< binaries[i].length(); j++ ){
                    response.add(binaries[i].charAt(j)=='1');
                }
        }
        return response;
    }

}

