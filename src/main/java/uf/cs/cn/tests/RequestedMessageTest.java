package uf.cs.cn.tests;

import org.junit.Assert;
import org.junit.Test;
import uf.cs.cn.message.MessageType;
import uf.cs.cn.message.RequestMessage;

public class RequestedMessageTest {

    @Test
    public void testMessageLengthAndMessageType () throws Exception{
        RequestMessage requestedMessage  = new RequestMessage(100);
        byte[] message = requestedMessage.getEncodedMessage();
        Assert.assertTrue(5<=message.length);
        Assert.assertEquals(MessageType.REQUEST,message[4]);
    }

    //TODO: Test for checking the payload content if its creating correctly
    //TODO: Test for checking, if given a byte stream, converting it back to the requirement elements

}
