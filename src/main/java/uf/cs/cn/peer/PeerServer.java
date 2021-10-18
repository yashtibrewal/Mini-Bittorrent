package uf.cs.cn.peer;

import java.net.ServerSocket;

public class PeerServer extends Thread{
    private ServerSocket listening_socket;
    private int self_port;
    private int self_peer_id;

    public PeerServer(int self_port, int self_peer_id){
        this.self_port = self_port;
        this.self_peer_id = self_peer_id;
    }

    public void run() {
        listening_socket = null;
        boolean searchForConnection = true;
        try {
            listening_socket = new ServerSocket(self_port);
            while (searchForConnection) {
                IncomingConnectionHandler connHandler = new IncomingConnectionHandler(listening_socket.accept(), this.self_peer_id);
                connHandler.start();
            }
        }
        catch (Exception e) {
            // TODO: handle exception here
            e.printStackTrace();
        }
    }
}