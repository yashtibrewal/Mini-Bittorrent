package uf.cs.cn.peer;

import uf.cs.cn.message.HaveMessage;
import uf.cs.cn.message.PieceMessage;
import uf.cs.cn.message.RequestMessage;
import uf.cs.cn.utils.*;

import java.util.*;

/**
 * Peer represents a node in the P2P connection
 * A peer can act as a server, or as a client with other peers
 * It cannot act as a server and client with the same peer at the same time.
 */

public class Peer extends Thread {

    private static Peer peer;
    /**
     * List of neighbours I am interested in.
     */
    private static HashSet<Integer> preferredNeighborsList = new HashSet<>();
    /**
     * List of neighbours who are interested in me.
     */
    private static HashSet<Integer> interestedList = new HashSet<>();
    private final int self_peer_id;
    PriorityQueue<PeerConfig> priorityQueue = new PriorityQueue<>((a, b) -> b.download_bandwidth_data_counter - a.download_bandwidth_data_counter);
    // to keep the references to the objects in priority queue
    HashMap<Integer, PeerConfig> references = new HashMap<>();
    ArrayList<OutgoingConnection> outgoingConnections = new ArrayList<>();
    PeerServer peer_server;
    ArrayList<Boolean> self_file_chunks ;
    private final PeerLogging peerLogging;
    private static boolean close_connection = false;

    private Peer(int self_peer_id) {
        this.self_peer_id = self_peer_id;
        Peer.peer = this;
        self_file_chunks = new ArrayList<>();
        for(int i=0;i<BitFieldUtils.getNumberOfChunks();i++) {
            self_file_chunks.add(false);
        }
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

    public void addToInterested(int client_id){
        interestedList.add(client_id);
    }

    public static int getPeerId() {
        if(Peer.getInstance() == null) {
            return 0;
        }
        return Peer.getInstance().self_peer_id;
    }

    public HashSet<Integer> getInterestedList() {
        return interestedList;
    }

    public void setInterestedList(HashSet<Integer> interestedList) {
        this.interestedList = interestedList;
    }

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

    public void updateNeighbourFileChunk(int neighbour_id, int file_chunk_number) {
        try {
            references.get(neighbour_id).file_chunks.set(file_chunk_number-1,true);
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

    public void sendHaveMessages(int chunk_id) {
        // for every outgoing connection, send a have message.
        for (OutgoingConnection outgoingConnection : outgoingConnections) {
            try {
                outgoingConnection.objectOutputStream.write(new HaveMessage(chunk_id).getEncodedMessage());
                outgoingConnection.objectOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkAndSendNotInterestedForAllPeers() {
        for(PeerInfoConfigFileReader.PeerInfo peerInfo: PeerInfoConfigFileReader.getPeerInfoList()) {
            if (!Peer.getInstance().checkIfInterested(peerInfo.getPeer_id())){
                Peer.sendNotInterested(peerInfo.getPeer_id());
            }
        }
    }

    /**
     * Sends a request message to the client by selecting a RANDOM file chunk which the neighbour has, and I don't
     * @param client_peer_id
     */
    public void sendRequestMessage(int client_peer_id) {
        try {
            // since array was 0 indexed, and files pieces are 1 indexed, we are added 1 to get the correct file piece number
            int chunk_id = PeerUtils.pickRandomIndex(self_file_chunks,references.get(client_peer_id).file_chunks)+1;
            RequestMessage requestMessage = new RequestMessage(chunk_id);
            for(OutgoingConnection outgoingConnection: outgoingConnections) {
                if(outgoingConnection.getDestination_peer_id() == client_peer_id){
                    outgoingConnection.objectOutputStream.write(requestMessage.getEncodedMessage());
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

    public void markHasChokedMe(int client_peer_id) {
        references.get(client_peer_id).setHas_choked_me(true);
    }

    public void markHasUnChokedMe(int client_peer_id) {
        references.get(client_peer_id).setHas_choked_me(false);
    }

    public void updateNotInterested(int client_peer_id) {
        interestedList.remove(client_peer_id);
    }

    public void sendPieceMessage(int client_peer_id, int chunk_id) {
        for(OutgoingConnection outgoingConnection: outgoingConnections) {
            if(outgoingConnection.getDestination_peer_id() == client_peer_id) {
                try {
                    outgoingConnection.objectOutputStream.write(new PieceMessage(chunk_id).getEncodedMessage());
                    outgoingConnection.objectOutputStream.flush();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isClose_connection() {
        return close_connection;
    }

    public static void updateCloseConnection() {
        // if all peers have all the files, close the connection.
        for(Integer i:Peer.getInstance().references.keySet()){
            if(!PeerUtils.gotCompleteFile(Peer.getInstance().references.get(i).file_chunks)){
                return;
            }
        }
        if(!PeerUtils.gotCompleteFile(getInstance().self_file_chunks))return;
        Peer.close_connection = true;
    }

    static class PeerConfig {
        ArrayList<Boolean> file_chunks;
        int peer_id;
        private int download_bandwidth_data_counter;
        private boolean has_choked_me;

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
            file_chunks = new ArrayList<>();
            for(int i=0;i<BitFieldUtils.getNumberOfChunks();i++) {
                file_chunks.add(false);
            }
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

        public boolean isHas_choked_me() {
            return has_choked_me;
        }

        public void setHas_choked_me(boolean has_choked_me) {
            this.has_choked_me = has_choked_me;
        }
    }
}
