package uf.cs.cn.peer;

import uf.cs.cn.message.HaveMessage;
import uf.cs.cn.message.PieceMessage;
import uf.cs.cn.message.RequestMessage;
import uf.cs.cn.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Peer represents a node in the P2P connection
 * A peer can act as a server, or as a client with other peers
 * It cannot act as a server and client with the same peer at the same time.
 */

public class Peer extends Thread {

    /**
     * {@link Peer#preferredNeighborsList}
     * List of neighbours who are in this are the ones who we send unchoke message to, so that they can send us
     * the request message and we can send them back the requested piece.
     * All the members in this list for sure have had sent the interested message before.
     */
    public static HashSet<Integer> preferredNeighborsList = new HashSet<>();
    private static Peer peer;
    private static boolean close_connection = false;
    /**
     * {@link Peer#self_peer_id}
     * List of neighbours who are interested in me.
     */
    private final int self_peer_id;
    // to keep the references to the objects in priority queue
    /**
     * {@link Peer#priorityQueue}
     * The queue will keep the list of interested neighbours who are eligible to be the preferred neighbours.
     * It keeps them in a descending order of downloading content, for example if the peer has downloaded more data
     * from Peer A and less data from Peer B, then Peer A will pop from the queue first so that we can add it to the
     * preferred neighbours.
     */
    PriorityQueue<PeerConfig> priorityQueue = new PriorityQueue<>((a, b) -> b.download_bandwidth_data_counter - a.download_bandwidth_data_counter);
    /**
     * {@link Peer#references}
     * Stores the references for all object references to the
     */
    HashMap<Integer, PeerConfig> references = new HashMap<>();
    /**
     * {@link Peer#outgoingConnections}
     * Keeps references to all the outgoing connections so that in case we have to boradcase a message like have,
     * we can iterate over it and send it to all connection.
     */
    ArrayList<OutgoingConnection> outgoingConnections = new ArrayList<>();
    /**
     *
     */
    PeerServer peer_server;
    /**
     * {@link Peer#self_file_chunks}
     * Keeps the list of chunks marked true which the present peer has.
     */
    ArrayList<Boolean> self_file_chunks;

    private Peer(int self_peer_id) {
        this.self_peer_id = self_peer_id;
        Peer.peer = this;
        self_file_chunks = new ArrayList<>();
        boolean isServer = PeerInfoConfigFileReader.isPeerServer(Peer.getPeerId());
        for (int i = 0; i < BitFieldUtils.getNumberOfChunks(); i++) {
            self_file_chunks.add(isServer);
        }
    }

    /**
     * {@link Peer#allPeersReceivedAllChunks()}
     * returns true if all the peers have received all the files including the present peer.s
     *
     * @return
     */
    public static boolean allPeersReceivedAllChunks() {
        for (Integer peer_id : Peer.getInstance().references.keySet()) {
            if (!Peer.getInstance().references.get(peer_id).gotAllChunks()) return false;
        }
        return Peer.getInstance().gotCompleteFile();
    }

    public static void sendInterested(int client_peer_id) {
        // TODO: make it efficient by adding a break in a manual loop by keeping the reference in PeerConfig class.
        Peer.getInstance().outgoingConnections.forEach((outgoingConnection -> {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                outgoingConnection.sendInterestedMessages();
            }
        }));
    }

    public static void sendNotInterested(int client_peer_id) {
        Peer.getInstance().outgoingConnections.forEach((outgoingConnection -> {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                Peer.getInstance().references.get(client_peer_id).is_interested = !Peer.getInstance().references.get(client_peer_id).is_interested && Peer.getInstance().references.get(client_peer_id).is_interested;
                outgoingConnection.sendNotInterestedMessages();
            }
        }));
    }

    public static Peer getInstance() {
        return Peer.peer;
    }

    /**
     * {@link Peer#getInstance(int)}
     * Note: v is called twice, we are losing the previous object.
     *
     * @return
     */
    public static Peer getInstance(int self_peer_id) {
        //TODO: Throw exception if object already exists
        Peer.peer = new Peer(self_peer_id);
        return Peer.peer;
    }

    public static int getPeerId() {
        if (Peer.getInstance() == null) {
            return 0;
        }
        return Peer.getInstance().self_peer_id;
    }

    public static boolean isClose_connection() {
        return close_connection;
    }

    public static void updateCloseConnection() {
        // if we are not have references for all other peers, return false
        if (getInstance().references.size() < PeerInfoConfigFileReader.getPeerInfoList().size() - 1) return;
        // if all peers have all the files, close the connection.
        for (Integer i : Peer.getInstance().references.keySet()) {
            if (!PeerUtils.gotCompleteFile(Peer.getInstance().references.get(i).file_chunks)) {
                return;
            }
        }
        if (!PeerUtils.gotCompleteFile(getInstance().self_file_chunks)) return;
        Peer.close_connection = true;
    }

    public void addToPriorityQueueIfInterested(int client_id) {
        if (!priorityQueue.contains(references.get(client_id))
                && references.get(client_id).is_interested)
            priorityQueue.add(references.get(client_id));
    }

    public void addToInterested(int client_id) {
        System.out.println("----Adding to interested " + client_id);
        Peer.getInstance().references.get(client_id).is_interested = true;
        addToPriorityQueueIfInterested(client_id);
    }

    public HashSet<Integer> getPreferredNeighborsList() {
        return preferredNeighborsList;
    }

    public int getSelf_peer_id() {
        return self_peer_id;
    }

    /**
     * Purpose of the function is to connect to all peers' which are already in the network.
     * Logic: Traverse through the list of connections from the file. Since the file is getting started in the sequence
     * the peers before this peer are already started, and hence we initiate connection with them.
     */
    public void establishOutgoingConnections() {
        // TODO: Poll this array every x time units and check the connection.
        for (PeerInfoConfigFileReader.PeerInfo peerInfo : PeerInfoConfigFileReader.getPeerInfoList()) {
            if (peerInfo.getPeer_id() != this.self_peer_id) {
                System.out.println("INITIATING connection to "
                        + peerInfo.getPeer_host_name() + " at port " + peerInfo.getListening_port());
                PeerLogging.getInstance().outgoingTCPConnectionLog(String.valueOf(peerInfo.getPeer_id()));
                //TODO: Store the object references when looping for future use
                OutgoingConnection outgoingConnection = new OutgoingConnection(
                        peerInfo.getPeer_host_name(),
                        peerInfo.getListening_port(),
                        this.self_peer_id, peerInfo.getPeer_id()
                );
                outgoingConnections.add(outgoingConnection);
                outgoingConnection.start();
            }
        }
    }

    /**
     * Purpose of the function is to start a server socket for the peer so that other peers can connect to this peer.
     */
    public void runServer() {
        // read peer id from file
        try {
            this.peer_server = new PeerServer(
                    PeerInfoConfigFileReader.getPortForPeerID(
                            this.self_peer_id),
                    this.self_peer_id);
            peer_server.start();
        } catch (Exception e) {
            // TODO: handle the exception
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Starting peer " + self_peer_id + " as a server");
        this.runServer();
        System.out.println("Starting peer " + self_peer_id + " as a client");
        this.establishOutgoingConnections();

    }

    /**
     * gotCompleteFile returns true if all pieces exists, if even one piece is missing, it returns false.
     */
    public boolean gotCompleteFile() {
        return PeerUtils.gotCompleteFile(self_file_chunks);
    }

    /**
     * Used to set the neighbour's list of file chunks.
     *
     * @param neighbour_id
     * @param neighbour_chunk
     */
    synchronized public void updateNeighbourFileChunk(int neighbour_id, ArrayList<Boolean> neighbour_chunk) {
        try {
            if (references.containsKey(neighbour_id)) {
                references.get(neighbour_id).file_chunks = neighbour_chunk;
            } else {
                references.put(neighbour_id, new PeerConfig(neighbour_id));
                references.get(neighbour_id).file_chunks = neighbour_chunk;
                addToPriorityQueueIfInterested(neighbour_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the particular chunk of the neighbour.
     *
     * @param neighbour_id
     * @param file_chunk_number
     */
    public void updateNeighbourFileChunk(int neighbour_id, int file_chunk_number) {
        try {
            references.get(neighbour_id).file_chunks.set(file_chunk_number - 1, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSelfFileChunk(int piece_id) {
        self_file_chunks.set(piece_id - 1, true);
        System.out.println("Self File chunk updated to : " + self_file_chunks);
    }

    public int selfFileChunkCount() {
        int count = 0;
        for (boolean piece : self_file_chunks
        ) {
            if (piece) count++;
        }
        return count;
    }

    public int getMaxPossiblePreferredNeighbors() {
        return Math.min(CommonConfigFileReader.number_of_preferred_neighbours, PeerInfoConfigFileReader.numberOfPeers - 1);
    }

    synchronized public void rebuildHeap() {
        for (Integer i : preferredNeighborsList) {
            addToPriorityQueueIfInterested(i);
        }
        preferredNeighborsList.clear();
    }

    public int totalInterestedPeers() {
        return preferredNeighborsList.size() + priorityQueue.size();
    }

    synchronized public void calculatePreferredNeighbours() {

        if (priorityQueue.size() == 0) return;
        System.out.println("HEAP BUILD CALLED!");
        rebuildHeap();
        int k = PeerInfoConfigFileReader.numberOfPeers - 1;
        while (priorityQueue.size() > 0 && k > 0) {
            PeerConfig config = priorityQueue.poll();
            System.out.println("adding " + config.peer_id + " to the queue. ");
            preferredNeighborsList.add(config.peer_id);
            k--;
        }

        int num = (int) ((Math.random() * (totalInterestedPeers() - 1 - getMaxPossiblePreferredNeighbors())) + getMaxPossiblePreferredNeighbors());
        int ctr = 0;
        for (int it_value : preferredNeighborsList) {
            if (!preferredNeighborsList.contains(it_value)) {
                ctr++;
            }

            if (ctr == num) {
                preferredNeighborsList.add(it_value);
                break;
            }
        }
    }

    /**
     * This methods returns if the running peer is interested in the neighbour's chunks
     *
     * @param neighbor_peer_id
     * @return
     */
    public synchronized boolean checkIfInterested(int neighbor_peer_id) {
        ArrayList<Boolean> other_end = references.get(neighbor_peer_id).file_chunks;

        for (int i = 0; i < other_end.size() && i < BitFieldUtils.getNumberOfChunks(); i++) {
            if (other_end.get(i) && !self_file_chunks.get(i)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void resetDownloadCounters() {
        for (PeerConfig pc : references.values()) pc.resetCounter();
        priorityQueue.clear();
    }

    public void incrementDownloadCount(int client_peer_id) {
        references.get(client_peer_id).setDownload_bandwidth_data_counter(references.get(client_peer_id).download_bandwidth_data_counter + 1);
    }

    public void sendHaveMessages(int chunk_id) {
        // for every outgoing connection, send a have message.
        for (OutgoingConnection outgoingConnection : outgoingConnections) {
            try {
                byte[] output = new HaveMessage(chunk_id).getEncodedMessage();
                for (byte b : output) outgoingConnection.objectOutputStream.write(b);
                outgoingConnection.objectOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkAndSendNotInterestedForAllPeers() {
        for (PeerInfoConfigFileReader.PeerInfo peerInfo : PeerInfoConfigFileReader.getPeerInfoList()) {

            if (Peer.getInstance().self_peer_id == peerInfo.getPeer_id()) continue;

            if (!Peer.getInstance().checkIfInterested(peerInfo.getPeer_id())) {
                Peer.sendNotInterested(peerInfo.getPeer_id());
            }
        }
    }

    /**
     * Sends a request message to the client by selecting a RANDOM file chunk which the neighbour has, and I don't
     *
     * @param client_peer_id
     */
    synchronized public void sendRequestMessage(int client_peer_id) {
        try {
            // since array was 0 indexed, and files pieces are 1 indexed, we are added 1 to get the correct file piece number
            int chunk_id = PeerUtils.pickRandomIndex(self_file_chunks, references.get(client_peer_id).file_chunks) + 1;
            if (chunk_id == -1) {
                return;
            }
            RequestMessage requestMessage = new RequestMessage(chunk_id);
            for (OutgoingConnection outgoingConnection : outgoingConnections) {
                if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                    byte[] output = requestMessage.getEncodedMessage();
                    for (byte b : output) outgoingConnection.objectOutputStream.write(b);
                    outgoingConnection.objectOutputStream.flush();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addClientToInterestedMessage(int client_peer_id) {
        Peer.getInstance().addToInterested(client_peer_id);
    }

    public void updateNotInterested(int client_peer_id) {
        priorityQueue.remove(references.get(client_peer_id));

        Peer.getInstance().references.get(client_peer_id).is_interested = false;
    }

    synchronized public void sendPieceMessage(int client_peer_id, int chunk_id) {
        for (OutgoingConnection outgoingConnection : outgoingConnections) {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                try {
                    byte[] output = new PieceMessage(chunk_id).getEncodedMessage();
                    for (byte b : output) outgoingConnection.objectOutputStream.write(b);
                    outgoingConnection.objectOutputStream.flush();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
