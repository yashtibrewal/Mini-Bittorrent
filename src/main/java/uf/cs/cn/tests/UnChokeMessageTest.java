package uf.cs.cn.tests;

import org.junit.Assert;
import org.junit.Test;
import uf.cs.cn.message.MessageType;
import uf.cs.cn.message.UnChokeMessage;

public class UnChokeMessageTest {

    @Test
    public void testMessageLengthAndMessageType () throws Exception{
        UnChokeMessage unChokeMessage = new UnChokeMessage();
        byte[] message = unChokeMessage.getEncodedMessage();
        Assert.assertEquals(5,message.length);
        Assert.assertEquals(0,message[0]);
        Assert.assertEquals(0,message[1]);
        Assert.assertEquals(0,message[2]);
        Assert.assertEquals(1,message[3]);
        Assert.assertEquals(MessageType.UN_CHOKE,message[4]);
    }

    //TODO: Test for checking, if given a byte stream, converting it back to the requirement elements
}
