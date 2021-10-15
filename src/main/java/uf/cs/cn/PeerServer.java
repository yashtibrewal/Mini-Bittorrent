package uf.cs.cn;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class PeerServer implements Runnable {
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

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

    }
}
