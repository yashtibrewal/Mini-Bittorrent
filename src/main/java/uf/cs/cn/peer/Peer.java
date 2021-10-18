package uf.cs.cn.peer;

import uf.cs.cn.utils.CommonConfigFileReader;
import uf.cs.cn.utils.PeerInfoConfigFileReader;

import java.util.ArrayList;

/**
 * Peer represents a node in the P2P connection
 * A peer can act as a server, or as a client with other peers
 * It cannot act as a server and client with the same peer at the same time.
 */

public class Peer extends Thread{

    // TODO: read the port from the file instead of harcoding here
    private ArrayList<Integer> neighbour_ids;
    // Handshake message will be common for client and server
    PeerServer peer_server;
    private int self_peer_id;
    public boolean is_server;

    public  Peer(boolean is_server, int self_peer_id){
        this.is_server = is_server;
        this.self_peer_id = self_peer_id;
    }

    /**
     * Purpose of the function is to connect to all peers' servers
     */
    public void establishOutgoingConnections() {
        // TODO: Poll this array every x mins and check the connection.
        PeerInfoConfigFileReader.getPeerInfoList();
        for(PeerInfoConfigFileReader.PeerInfo peerInfo: PeerInfoConfigFileReader.getPeerInfoList()) {
            if(peerInfo.getPeer_id() != this.self_peer_id) {
                // Store the object references when looping for future use
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
        // TODO: un - hard code this peer id

        int server_port = 3000;
        try{
            this.peer_server = new PeerServer(server_port, this.self_peer_id);
            peer_server.start();
        } catch (Exception e) {
            // TODO: handle the exception
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if(is_server) {
            System.out.println("Starting server");
            this.runServer();
        } else {
            this.establishOutgoingConnections();
            System.out.println("Starting client");
        }
    }

}
