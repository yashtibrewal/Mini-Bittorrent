package uf.cs.cn.utils;

import uf.cs.cn.message.ActualMessage;
import uf.cs.cn.message.MessageType;

public class MessageParser {

    public static void parse(ActualMessage actualMessage) {

        switch(actualMessage.getMessage_type())
        {
            case MessageType.UN_CHOKE:
                break;

            case MessageType.CHOKE:
                break;

            case MessageType.INTERESTED:
                break;

            case MessageType.NOT_INTERESTED:
                break;

            case MessageType.HAVE:
                break;

            case MessageType.BIT_FIELD:
                break;

            case MessageType.REQUEST:
                break;

            case MessageType.PIECE:
                break;
        }

    }
}
