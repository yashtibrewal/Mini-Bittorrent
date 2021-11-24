package uf.cs.cn.tests;

import org.junit.Assert;
import org.junit.Test;
import uf.cs.cn.message.InterestedMessage;
import uf.cs.cn.message.MessageType;

public class InterestedMessageTest {

    @Test
    public void testCheckHandshakePaddingMessage1 () throws Exception{
        InterestedMessage message = new InterestedMessage();
        byte[] result = message.getInterestedMessageBytes();
        Assert.assertEquals(5, result.length); // 4 bytes for the message length field, and 1 byte for message type
        Assert.assertEquals((char)result[4],MessageType.INTERESTED); // last byte, i.e. 4th position should be 2
        Assert.assertEquals((char)result[3],1); // since the message length is 1, we have 1 on the 3rd pos and so on
        Assert.assertEquals((char)result[2],0);
        Assert.assertEquals((char)result[2],0);
        Assert.assertEquals((char)result[2],0);

    }
    //TODO: Test for checking, if given a byte stream, converting it back to the requirement elements

}
