package uf.cs.cn.tests;

import org.junit.Assert;
import org.junit.Test;
import uf.cs.cn.utils.HandShakeMessageUtils;

public class HandShakeMessageUtilsTest {

    @Test
    public void testCheckHandshakePaddingMessage1 () throws Exception{
        boolean result;
        result = HandShakeMessageUtils.checkHandshakePaddingMessage(
                "P2PFILESHARINGPROJ00000000001000".getBytes()
        );
        Assert.assertTrue(result);
    }

    @Test
    public void testCheckHandshakePaddingMessage2 () throws Exception{
        boolean result;
        result = HandShakeMessageUtils.checkHandshakePaddingMessage(
                "P2PFILESHARINGPROJ00000000011000".getBytes()
        );
        Assert.assertFalse(result);
    }
    @Test
    public void testCheckHandshakeHeaderMessage1 () throws Exception{
        boolean result;
        result = HandShakeMessageUtils.checkHandshakeHeaderMessage(
                "P2PFILESHARINGPROJ00000000001000".getBytes()
        );
        Assert.assertTrue(result);
    }
    @Test
    public void testCheckHandshakeHeaderMessage2 () throws Exception{
        boolean result;
        result = HandShakeMessageUtils.checkHandshakeHeaderMessage(
                "P2PFILESHARINGPROA00000000001000".getBytes()
        );
        Assert.assertFalse(result);
    }
    @Test
    public void testcheckPeerId1 () throws Exception{
        boolean result;
        result = HandShakeMessageUtils.validatePeerId(
                "P2PFILESHARINGPROJ00000000001000".getBytes()
        );
        Assert.assertTrue(result);
    }
    @Test
    public void testcheckPeerId2 () throws Exception{
        boolean result;
        result = HandShakeMessageUtils.validatePeerId(
                "P2PFILESHARINGPROJ0000000000100A".getBytes()
        );
        Assert.assertFalse(result);
    }
}
