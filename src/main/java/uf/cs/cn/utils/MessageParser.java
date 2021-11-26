package uf.cs.cn.utils;

import uf.cs.cn.message.ActualMessage;
import uf.cs.cn.message.MessageType;
import uf.cs.cn.peer.Peer;

import java.nio.file.Paths;

public class MessageParser {
    static PeerLogging logger = new PeerLogging();

    public static void parse(ActualMessage actualMessage, Peer peer) {

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
                if(Peer.getInstance().gotCompleteFile()){
                    String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
                    String peer_id = String.valueOf(PeerInfoConfigFileReader.getPeerInfoList().get(0).getPeer_id());
                    FileMerger.mergeFile(
                            Paths.get(running_dir, peer_id, CommonConfigFileReader.file_name).toString(),
                            Paths.get(running_dir, peer_id).toString());
                }
                break;

            default:
                logger.genericErrorLog("Wrong Message Type received - Cannot parse");
        }

    }
}
