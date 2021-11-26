package uf.cs.cn.message;

import java.io.IOException;

public class UnChokeMessage extends ActualMessage{

    public UnChokeMessage() throws Exception {
        super(1, MessageType.UN_CHOKE);
    }
}
