package uf.cs.cn.peer;

import uf.cs.cn.utils.BitFieldUtils;
import uf.cs.cn.utils.PeerInfoConfigFileReader;
import uf.cs.cn.utils.PeerLogging;
import uf.cs.cn.utils.PeerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Peer represents a node in the P2P connection
 * A peer can act as a server, or as a client with other peers
 * It cannot act as a server and client with the same peer at the same time.
 */

public class Peer extends Thread{

    PriorityQueue<PeerConfig> priorityQueue = new PriorityQueue<>((a, b) -> b.download_bandwidth_data_counter - a.download_bandwidth_data_counter);

    // to keep the references to the objects in priority queue
    HashMap<Integer, PeerConfig> references = new HashMap<>();

    static class PeerConfig{
        ArrayList<Boolean> file_chunks;
        int peer_id;
        public int getDownload_bandwidth_data_counter() {
            return download_bandwidth_data_counter;
        }
        public void setDownload_bandwidth_data_counter(int download_bandwidth_data_counter) {
            this.download_bandwidth_data_counter = download_bandwidth_data_counter;
        }
        private int download_bandwidth_data_counter;
        PeerConfig(int peer_id) throws Exception {
            this.peer_id = peer_id;
            file_chunks=new ArrayList<>(BitFieldUtils.getNumberOfChunks());
            download_bandwidth_data_counter++;
        }
        void resetCounter(){
            this.download_bandwidth_data_counter = 0;
        }

        void setFileChunkTrue(int index){
            file_chunks.set(index-1,true);
        }
        boolean hasAllChunks() {
            return PeerUtils.gotCompleteFile(this.file_chunks);
        }
    }

    PeerServer peer_server;

    public int getSelf_peer_id() {
        return self_peer_id;
    }

    private int self_peer_id;
    private PeerLogging peerLogging;
    private static Peer peer;
    ArrayList<Boolean> self_file_chunks;

    /**
     * Note: If getInstance(self_peer_id) is called twice, we are losing the previous object.
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

    private Peer(int self_peer_id){
        this.self_peer_id = self_peer_id;
        Peer.peer = this;
        peerLogging = new PeerLogging();
    }

    public static int getPeerId(){
        return Peer.getInstance().self_peer_id;
    }

    /**
     * Purpose of the function is to connect to all peers' servers
     */
    public void establishOutgoingConnections() {
        // TODO: Poll this array every x mins and check the connection.
        PeerInfoConfigFileReader.getPeerInfoList();
        for(PeerInfoConfigFileReader.PeerInfo peerInfo: PeerInfoConfigFileReader.getPeerInfoList()) {
            if(peerInfo.getPeer_id() != this.self_peer_id) {
                peerLogging.outgoingTCPConnectionLog(String.valueOf(peerInfo.getPeer_id()));
                //TODO: Store the object references when looping for future use
                OutgoingConnection outgoingConnection = new OutgoingConnection(
                        peerInfo.getPeer_host_name(),
                        peerInfo.getListening_port(),
                        this.self_peer_id, peerInfo.getPeer_id()
                );
                outgoingConnection.start();
            }
        }
    }

    /**
     * Purpose of the function is to listen to all incoming peer client requests
     */
    public void runServer() {
        // read peer id from file
        try{
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

    public boolean gotCompleteFile() {
        //TODO: Implement check of boolean piece array

        for(boolean piece: self_file_chunks){
            if(!piece) return false;
        }
        return true;
    }

    public void updateNeighbourFileChunk(int neighbour_id, ArrayList<Boolean> neighbour_chunk){
        try {

            if(references.containsKey(neighbour_id)){
                references.get(neighbour_id).file_chunks = neighbour_chunk;
            }else{
                references.put(neighbour_id,new PeerConfig(neighbour_id));
                references.get(neighbour_id).file_chunks = neighbour_chunk;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
