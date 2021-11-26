package uf.cs.cn.peer;

import uf.cs.cn.utils.PeerInfoConfigFileReader;
import uf.cs.cn.utils.PeerLogging;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Peer represents a node in the P2P connection
 * A peer can act as a server, or as a client with other peers
 * It cannot act as a server and client with the same peer at the same time.
 */

public class Peer extends Thread{

    private HashMap<Integer,ArrayList<Boolean>> neighbour_file_chunks;
    private ArrayList<Boolean> self_file_chunks;

    // TODO: read the port from the file instead of hard coding here
    private ArrayList<Integer> neighbour_ids;
    // Handshake message will be common for client and server
    PeerServer peer_server;
    private int self_peer_id;
    private PeerLogging peerLogging;
    private static Peer peer;

    /**
     * Note: If getInstance(self_peer_id) is called twice, we are losing the previous object.
     * @return
     */
    public static Peer getInstance() {
        return Peer.peer;
    }

    public static Peer getInstance(int self_peer_id) {
        Peer.peer = new Peer(self_peer_id);
        return Peer.peer;
    }

    private Peer(int self_peer_id){
        this.self_peer_id = self_peer_id;
        peerLogging = new PeerLogging(String.valueOf(self_peer_id));
        Peer.peer = this;
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
        return true;
    }
}
