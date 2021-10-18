package uf.cs.cn.peer;

import uf.cs.cn.utils.PeerInfoConfigFileReader;

import java.util.ArrayList;

/**
 * Peer represents a node in the P2P connection
 * A peer can act as a server, or as a client with other peers
 * It cannot act as a server and client with the same peer at the same time.
 */

public class Peer extends Thread{

    // TODO: read the port from the file instead of harcoding here
    private final int server_port = 3000;
    private ArrayList<Integer> neighbour_ids;
    // Handshake message will be common for client and server
    private ArrayList<IncomingConnection> peer_server = new ArrayList<>();
    public boolean is_server;

    public  Peer(boolean is_server){
        this.is_server = is_server;
    }

    /**
     * Purpose of the function is to connect to all peers' servers
     * @param args
     */
    public void establishOutgoingConnections(String[] args) {
        // TODO: Loop through all listening ports from config file and connect to peer's servers
        // TODO: read this peer's id from  a file
        // TODO: Add peer_id in constructor
        // TODO: Poll this array every x mins and check the connection.
        PeerInfoConfigFileReader.getPeerInfoList();
        int peer_id = 1000;
        // Store the object references when looping for future use
        OutgoingConnection outgoingConnection = new OutgoingConnection("localhost", 3000, 1000, 1001);
        outgoingConnection.start();
        //
    }

    /**
     * Purpose of the function is to listen to all incoming peer client requests
     * @param args
     */
    public void runServer(String[] args) {
        // read peer id from file
        int peer_id = 1000;
        try{
            //TODO: Keep one object, and not an array.
            peer_server.add(new IncomingConnection(server_port, peer_id));
            peer_server.get(peer_server.size()-1).start();
        } catch (Exception e) {
            // TODO: handle the exception
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if(is_server) {
            System.out.println("Starting server");
            this.runServer(new String[]{});
        } else {
            this.establishOutgoingConnections(new String[]{});
            System.out.println("Starting client");
        }
    }

}
