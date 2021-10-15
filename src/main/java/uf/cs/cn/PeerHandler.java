package uf.cs.cn;

import java.net.Socket;

public class PeerHandler implements Runnable {
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

    public PeerHandler(Socket listener, PeerAdmin admin) {
    }
    public void setEndPeerID(String pid) {
    }
}
