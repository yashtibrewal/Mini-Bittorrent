package uf.cs.cn.tests;

import org.junit.Assert;
import org.junit.Test;
import uf.cs.cn.utils.PeerUtils;

import java.util.ArrayList;

public class PeerUtilsTest {

//    @Test
//    public void gotCompleteFile() {
//    }

    @Test
    public void pickRandomIndex1() {
        ArrayList<Boolean> list1 = new ArrayList<>();
        ArrayList<Boolean> list2 = new ArrayList<>();
        list1.add(true);
        list1.add(true);
        list1.add(true);
        list2.add(false);
        list2.add(true);
        list2.add(true);
        Assert.assertEquals(0,PeerUtils.pickRandomIndex(list1,list2));
    }

    public boolean inRange(int value, int min, int max){
        return value >= min && (value <= max);
    }

    @Test
    public void pickRandomIndex2() {
        ArrayList<Boolean> list1 = new ArrayList<>();
        ArrayList<Boolean> list2 = new ArrayList<>();
        list1.add(true);
        list1.add(true);
        list1.add(true);
        list2.add(false);
        list2.add(false);
        list2.add(false);
        Assert.assertTrue(inRange(PeerUtils.pickRandomIndex(list1,list2),0,2));
    }
}