package uf.cs.cn.message;

import uf.cs.cn.peer.OutgoingConnection;
import uf.cs.cn.peer.Peer;
import uf.cs.cn.utils.PeerLogging;
import uf.cs.cn.utils.PeerUtils;

import java.util.ArrayList;

/**
 * Mainly Used to send all messages
 */
public class MessageSender {
    public static void sendInterested(int client_peer_id) {
        // TODO: make it efficient by adding a break in a manual loop by keeping the reference in PeerConfig class.
        Peer.getInstance().getOutgoingConnections().forEach((outgoingConnection -> {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                outgoingConnection.sendInterestedMessages();
            }
        }));
    }

    public static void sendNotInterested(int client_peer_id) {
        Peer.getInstance().getOutgoingConnections().forEach((outgoingConnection -> {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                Peer.getInstance().getReferences().get(client_peer_id).setIs_interested(
                        !Peer.getInstance().getReferences().get(client_peer_id).isIs_interested()
                                && Peer.getInstance().getReferences().get(client_peer_id).isIs_interested());
                outgoingConnection.sendNotInterestedMessages();
            }
        }));
    }

    /**
     * Sends a request message to the client by selecting a RANDOM file chunk which the neighbour has, and I don't
     *
     * @param client_peer_id
     */
    synchronized public static void sendRequestMessage(ArrayList<Boolean> self_file_chunks, int client_peer_id) {
        try {
            // since array was 0 indexed, and files pieces are 1 indexed, we are added 1 to get the correct file piece number
            int chunk_id = PeerUtils.pickRandomIndex(self_file_chunks,
                    Peer.getInstance().getReferences().get(client_peer_id).getFile_chunks()) + 1;
            if (chunk_id == -1) {
                return;
            }
            RequestMessage requestMessage = new RequestMessage(chunk_id);
            for (OutgoingConnection outgoingConnection : Peer.getInstance().getOutgoingConnections()) {
                if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                    byte[] output = requestMessage.getEncodedMessage();
                    for (byte b : output) outgoingConnection.getObjectOutputStream().write(b);
                    outgoingConnection.getObjectOutputStream().flush();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendHaveMessages(int chunk_id) {
        // for every outgoing connection, send a have message.
        for (OutgoingConnection outgoingConnection : Peer.getInstance().getOutgoingConnections()) {
            try {
                byte[] output = new HaveMessage(chunk_id).getEncodedMessage();
                for (byte b : output) outgoingConnection.getObjectOutputStream().write(b);
                outgoingConnection.getObjectOutputStream().flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized public static void sendPieceMessage(int client_peer_id, int chunk_id) {
        for (OutgoingConnection outgoingConnection : Peer.getInstance().getOutgoingConnections()) {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                try {
                    byte[] output = new PieceMessage(chunk_id).getEncodedMessage();
                    for (byte b : output) outgoingConnection.getObjectOutputStream().write(b);
                    outgoingConnection.getObjectOutputStream().flush();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    synchronized public static void sendChokesAndUnChokes() {

        System.out.println("Calculating preferred neighbours");
        Peer.getInstance().calculatePreferredNeighbours();
        System.out.println("-PREFERRED NEIGHBOURS are - " + Peer.preferredNeighborsList);
        Peer.getInstance().resetDownloadCounters();
        System.out.println("-PRIORITY QUEUE is - " + Peer.getInstance().getPriorityQueue());
        Peer.getInstance().getPreferredNeighborsList().forEach((pN -> {
            Peer.getInstance().getOutgoingConnections().forEach((outgoingConnection -> {
                if (outgoingConnection.getDestination_peer_id() == pN) {
                    if (Peer.getInstance().getPreferredNeighborsList().contains(pN)) {
                        outgoingConnection.sendUnChokeMessages();
                    } else if (!Peer.getInstance().getPreferredNeighborsList().contains(pN))
                        outgoingConnection.sendChokeMessages();
                }
            }));
        }));
        PeerLogging.getInstance().changeOfPreferredNeighboursLog(new ArrayList<>(Peer.preferredNeighborsList));
    }

}
