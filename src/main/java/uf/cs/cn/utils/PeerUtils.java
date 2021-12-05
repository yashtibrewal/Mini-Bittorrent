package uf.cs.cn.utils;

import java.util.ArrayList;

public class PeerUtils {
    public static boolean gotCompleteFile(ArrayList<Boolean> list) {
        for (boolean piece : list) {
            if (!piece) return false;
        }
        return true;
    }

    /**
     * @param list1 file chunk representation in boolean array of a peer.
     * @param list2 file chunk representation in boolean array of other peer
     * @return random index of a chunk which is IN file 1 and NOT IN file 2.
     */
    public static int pickRandomIndex(ArrayList<Boolean> list1, ArrayList<Boolean> list2) {
        ArrayList<Integer> bag = new ArrayList<>();
//        System.out.println("LIST 1 :self is " + list1);
//        System.out.println("LIST 2 :self is " + list2);
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i) && list2.get(i)) {
                bag.add(i);
            }
        }
        if(bag.size() == 0){
            return -1;
        }
//        System.out.println("BAG IS " + bag);
        int random = (int) (Math.random() * bag.size());
//        System.out.println("Random selected is " + random);
        int result = bag.get(random);
//        System.out.println("Value selected is " + result);
        return result;
    }
}
