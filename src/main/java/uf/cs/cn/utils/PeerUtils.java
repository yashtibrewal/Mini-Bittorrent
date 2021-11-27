package uf.cs.cn.utils;

import java.util.ArrayList;

public class PeerUtils {
    public static boolean gotCompleteFile(ArrayList<Boolean> list) {
        for(boolean piece: list){
            if(!piece) return false;
        }
        return true;
    }
}
