package uf.cs.cn;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class PeerServer {
    private String peerID;
    private ServerSocket listener;
    private PeerAdmin peerAdmin;
    private boolean dead;

    public PeerServer(String peerID, ServerSocket listener, PeerAdmin admin) {
        this.peerID = peerID;
        this.listener = listener;
        this.peerAdmin = admin;
        this.dead = false;
    }
}
