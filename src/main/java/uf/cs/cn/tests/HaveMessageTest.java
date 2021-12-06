package uf.cs.cn.tests;

import org.junit.Assert;
import org.junit.Test;
import uf.cs.cn.message.HaveMessage;
import uf.cs.cn.message.MessageType;

import java.util.Arrays;

public class HaveMessageTest {

    @Test
    public void testHaveLengthAndMessageType() throws Exception {
        HaveMessage haveMessage = new HaveMessage(1);
        byte[] message = haveMessage.getEncodedMessage();
        Assert.assertEquals(9, message.length);
        Assert.assertEquals(0, message[0]);
        Assert.assertEquals(0, message[1]);
        Assert.assertEquals(0, message[2]);
        Assert.assertEquals(5, message[3]);
        Assert.assertEquals(MessageType.HAVE, message[4]);
    }

    @Test
    public void testHaveMessageStreamConvert() throws Exception {
        HaveMessage haveMessage = new HaveMessage(1);
        HaveMessage h2 = new HaveMessage(Arrays.copyOfRange(haveMessage.getEncodedMessage(), 4, 9));
        Assert.assertEquals(1, h2.getPieceIndex());

    }
    //TODO: Test for checking, if given a byte stream, converting it back to the requirement elements

}
