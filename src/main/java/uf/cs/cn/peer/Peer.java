package uf.cs.cn.peer;

import uf.cs.cn.utils.*;

import java.util.*;

/**
 * Peer represents a node in the P2P connection
 * A peer can act as a server, or as a client with other peers
 * It cannot act as a server and client with the same peer at the same time.
 */

public class Peer extends Thread {

    private static Peer peer;
    private static PeerConfig peerConfig;
    //private static HashSet<Integer> unchokedList = new HashSet<>();
    private static HashSet<Integer> preferredNeighborsList = new HashSet<>();
    private static HashSet<Integer> interestedList = new HashSet<>();
    private final int self_peer_id;
    PriorityQueue<PeerConfig> priorityQueue = new PriorityQueue<>((a, b) -> b.download_bandwidth_data_counter - a.download_bandwidth_data_counter);
    // to keep the references to the objects in priority queue
    HashMap<Integer, PeerConfig> references = new HashMap<>();
    ArrayList<OutgoingConnection> outgoingConnections = new ArrayList<>();
    PeerServer peer_server;
    ArrayList<Boolean> self_file_chunks;
    private PeerLogging peerLogging;

    private Peer(int self_peer_id) {
        this.self_peer_id = self_peer_id;
        Peer.peer = this;
        peerLogging = PeerLogging.getInstance();
    }

    public static void sendInterested(int client_peer_id) {
        // TODO: make it efficient by adding a break in a manual loop - NOT POSSIBLE WITH STREAMS
        Peer.getInstance().outgoingConnections.forEach((outgoingConnection -> {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                outgoingConnection.sendInterestedMessages();
            }
        }));
    }

    public static void sendNotInterested(int client_peer_id) {
        Peer.getInstance().outgoingConnections.forEach((outgoingConnection -> {
            if (outgoingConnection.getDestination_peer_id() == client_peer_id) {
                if (interestedList.contains(client_peer_id)) interestedList.remove(client_peer_id);
                outgoingConnection.sendNotInterestedMessages();
            }
        }));
    }

    /**
     * Note: If getInstance(self_peer_id) is called twice, we are losing the previous object.
     *
     * @return
     */
    public static Peer getInstance() {
        return Peer.peer;
    }

    public static Peer getInstance(int self_peer_id) {
        //TODO: Throw exception if object already exists
        Peer.peer = new Peer(self_peer_id);
        return Peer.peer;
    }

    public static int getPeerId() {
        return Peer.getInstance().self_peer_id;
    }

    public HashSet<Integer> getInterestedList() {
        return interestedList;
    }

    public void setInterestedList(HashSet<Integer> interestedList) {
        this.interestedList = interestedList;
    }

    /*public HashSet<Integer> getUnchokedList() {
        return unchokedList;
    }

    public void setUnchokedList(HashSet<Integer> unchokedList) {
        this.unchokedList = unchokedList;
    }*/

    public HashSet<Integer> getPreferredNeighborsList() {
        return preferredNeighborsList;
    }

    public void setPreferredNeighborsList(HashSet<Integer> preferredNeighborsList) {
        this.preferredNeighborsList = preferredNeighborsList;
    }

    public int getSelf_peer_id() {
        return self_peer_id;
    }

    /**
     * Purpose of the function is to connect to all peers' servers
     */
    public void establishOutgoingConnections() {
        // TODO: Poll this array every x mins and check the connection.
        PeerInfoConfigFileReader.getPeerInfoList();
        for (PeerInfoConfigFileReader.PeerInfo peerInfo : PeerInfoConfigFileReader.getPeerInfoList()) {
            if (peerInfo.getPeer_id() != this.self_peer_id) {
                peerLogging.outgoingTCPConnectionLog(String.valueOf(peerInfo.getPeer_id()));
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
     * Purpose of the function is to listen to all incoming peer client requests
     */
    public void runServer() {
        // read peer id from file
        try {
            this.peer_server = new PeerServer(PeerInfoConfigFileReader.getPortForPeerID(this.self_peer_id), this.self_peer_id);
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

        new ChokeHandler().startJob();
    }

    /**
        gotCompleteFile returns true if all pieces exists, if even one piece is missing, it returns false.
     */
    public boolean gotCompleteFile() {
        for (boolean piece : self_file_chunks) {
            if (!piece) return false;
        }
        return true;
    }

    public void updateNeighbourFileChunk(int neighbour_id, ArrayList<Boolean> neighbour_chunk) {
        try {
            if (references.containsKey(neighbour_id)) {
                references.get(neighbour_id).file_chunks = neighbour_chunk;
            } else {
                references.put(neighbour_id, new PeerConfig(neighbour_id));
                references.get(neighbour_id).file_chunks = neighbour_chunk;
                if(references.get(neighbour_id).is_interested)
                priorityQueue.offer(references.get(neighbour_id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSelfFileChunk(int piece_id) {
        self_file_chunks.set(piece_id, true);
    }

    public boolean isPreferredNeighbour(int neighbor_peer_id) {
        return preferredNeighborsList.contains(neighbor_peer_id);
    }

    /*public boolean isUnChokedNeighbour(int neighbor_peer_id) {
        return unchokedList.contains(neighbor_peer_id);
    }*/

    public void calculatePreferredNeighbours() {
        preferredNeighborsList.clear();
        for (int i = 0; i < CommonConfigFileReader.number_of_preferred_neighbours; i++) {
            PeerConfig config = priorityQueue.poll();
            preferredNeighborsList.add(config.peer_id);
        }

        int num = (int) ((Math.random() * (interestedList.size()-1 - CommonConfigFileReader.number_of_preferred_neighbours)) + CommonConfigFileReader.number_of_preferred_neighbours);
        int ctr = 0;
        Iterator<Integer> it = interestedList.iterator();
        while (it.hasNext()) {
            int it_value = it.next();
            if (!preferredNeighborsList.contains(it_value)) {
                ctr++;
            }
            if (ctr == num) {
                preferredNeighborsList.add(it_value);
                break;
            }
        }
    }

    public synchronized boolean checkIfInterested(int neighbor_peer_id) {
        ArrayList<Boolean> other_end = references.get(neighbor_peer_id).file_chunks;

        for (int i = 0; i < other_end.size() && i < BitFieldUtils.getNumberOfChunks(); i++) {
            if (other_end.get(i)  && !self_file_chunks.get(i)) {
                interestedList.add(neighbor_peer_id);
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

    static class PeerConfig {
        ArrayList<Boolean> file_chunks;
        int peer_id;
        private int download_bandwidth_data_counter;

        public boolean isIs_interested() {
            return is_interested;
        }

        public void setIs_interested(boolean is_interested) {
            this.is_interested = is_interested;
        }

        boolean is_interested;
        boolean is_unchoked;

        PeerConfig(int peer_id) throws Exception {
            this.peer_id = peer_id;
            file_chunks = new ArrayList<>(BitFieldUtils.getNumberOfChunks());
            download_bandwidth_data_counter++;
        }

        public int getDownload_bandwidth_data_counter() {
            return download_bandwidth_data_counter;
        }

        public void setDownload_bandwidth_data_counter(int download_bandwidth_data_counter) {
            this.download_bandwidth_data_counter = download_bandwidth_data_counter;
        }

        void resetCounter() {
            this.download_bandwidth_data_counter = 0;
        }

        void setFileChunkTrue(int index) {
            file_chunks.set(index - 1, true);
        }

        boolean hasAllChunks() {
            return PeerUtils.gotCompleteFile(this.file_chunks);
        }
    }
}
