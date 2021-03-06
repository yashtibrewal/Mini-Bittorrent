package uf.cs.cn.utils;

import uf.cs.cn.message.ActualMessage;
import uf.cs.cn.message.MessageSender;
import uf.cs.cn.message.MessageType;
import uf.cs.cn.message.PieceMessage;
import uf.cs.cn.peer.Peer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static uf.cs.cn.utils.FileMerger.deleteChunks;

public class MessageParser {
    static PeerLogging logger = PeerLogging.getInstance();

    public static void parse(ActualMessage actualMessage, int client_peer_id) throws IOException {
        if (actualMessage.getMessage_type() != MessageType.PIECE)
            System.out.println("MESSAGE TYPE " + actualMessage.getMessage_type() + " ARRAY " + Arrays.toString(actualMessage.getEncodedMessage()));
        else
            System.out.println("PIECE INDEX RECEIVED : " + actualMessage.convertByteArrayToInt(Arrays.copyOfRange(actualMessage.getPayload(), 0, 4)));
        int chunk_id;
        switch (actualMessage.getMessage_type()) {
            case MessageType.UN_CHOKE:
                // send request message
                if (!Peer.getInstance().gotCompleteFile()) {
                    MessageSender.sendRequestMessage(Peer.getInstance().getSelf_file_chunks(), client_peer_id);
                    logger.unChokingNeighbourLog(client_peer_id + "");
                }
                break;

            case MessageType.CHOKE:
                logger.chokingNeighbourLog(client_peer_id + "");
                break;

            case MessageType.INTERESTED:
                // add to the interested list
                Peer.getInstance().addClientToInterestedMessage(client_peer_id);
                logger.receivedInterestedLog(client_peer_id + "");
                break;

            case MessageType.NOT_INTERESTED:
                // remove from the interested
                Peer.getInstance().updateNotInterested(client_peer_id);
                logger.receiveNotInterested(client_peer_id + "");
                break;

            case MessageType.HAVE:
                // update the memory state of that particular client in our memory
                Peer.getInstance().updateNeighbourFileChunk(client_peer_id, actualMessage.convertByteArrayToInt(Arrays.copyOfRange(actualMessage.getPayload(), 0, 4)));
                // send if interested
                if (Peer.getInstance().checkIfInterested(client_peer_id)) MessageSender.sendInterested(client_peer_id);
                else MessageSender.sendNotInterested(client_peer_id);
                if (Peer.allPeersReceivedAllChunks()) {
                    deleteChunks();
                }
                logger.receivedHaveLog(client_peer_id + "", actualMessage.convertByteArrayToInt(Arrays.copyOfRange(actualMessage.getPayload(), 0, 4)));
                break;

            case MessageType.BIT_FIELD:
                // update peer memory
                Peer.getInstance().updateNeighbourFileChunk(client_peer_id, BitFieldUtils.convertToBoolArray(actualMessage.getPayload()));
                // trigger sending the interested message event
                if (Peer.getInstance().checkIfInterested(client_peer_id)) MessageSender.sendInterested(client_peer_id);
                else MessageSender.sendNotInterested(client_peer_id);

                HandShakeMessageUtils.setIncomingBitFieldCounter(HandShakeMessageUtils.getIncomingBitFieldCounter() + 1);
                break;


            case MessageType.REQUEST:
                // send a piece
                chunk_id = actualMessage.convertByteArrayToInt(Arrays.copyOfRange(actualMessage.getPayload(), 0, 4));
                MessageSender.sendPieceMessage(client_peer_id, chunk_id);
                break;

            case MessageType.PIECE:
                new PieceMessage(actualMessage.getEncodedMessage());
                chunk_id = actualMessage.convertByteArrayToInt(Arrays.copyOfRange(actualMessage.getPayload(), 0, 4));
                Peer.getInstance().updateSelfFileChunk(chunk_id);
                Peer.getInstance().incrementDownloadCount(client_peer_id);
                logger.downloadedPieceLog(client_peer_id + "", chunk_id, Peer.getInstance().selfFileChunkCount());
                // send have messages
                MessageSender.sendHaveMessages(chunk_id);
                // update not interested states and send if necessary
                Peer.getInstance().checkAndSendNotInterestedForAllPeers();
                if (Peer.getInstance().gotCompleteFile()) {
                    String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
                    String peer_id = String.valueOf(Peer.getInstance().getSelf_peer_id());
                    FileMerger.mergeFile(
                            Paths.get(running_dir, peer_id).toString(),
                            Paths.get(running_dir, peer_id, CommonConfigFileReader.file_name).toString());
                    logger.downloadingCompleteLog();

                }
                if (Peer.allPeersReceivedAllChunks()) {
                    deleteChunks();
                }
                break;

            default:
                logger.genericErrorLog("Wrong Message Type received - Cannot parse");
                System.out.println("Some other type of message is coming");
        }

    }
}
