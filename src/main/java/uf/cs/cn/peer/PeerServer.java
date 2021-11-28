package uf.cs.cn.peer;

import uf.cs.cn.utils.CommonConfigFileReader;
import uf.cs.cn.utils.FileSplitter;
import uf.cs.cn.utils.PeerInfoConfigFileReader;

import java.net.ServerSocket;
import java.nio.file.Paths;

public class PeerServer extends Thread {
    private ServerSocket listening_socket;
    private int self_port;
    private int self_peer_id;

    public PeerServer(int self_port, int self_peer_id) {
        this.self_port = self_port;
        this.self_peer_id = self_peer_id;
    }

    public void run() {
        listening_socket = null;
        boolean searchForConnection = true;
        try {
            listening_socket = new ServerSocket(self_port);
            boolean is_server = false;
            for(PeerInfoConfigFileReader.PeerInfo peerInfo: PeerInfoConfigFileReader.getPeerInfoList()){
                if(Peer.getPeerId() == peerInfo.getPeer_id() && peerInfo.isHas_file()){
                    is_server = true;
                }
            }
            if(is_server) {
                String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
                String peer_id = String.valueOf(self_peer_id);
                FileSplitter.splitFile(
                        Paths.get(running_dir, peer_id, CommonConfigFileReader.file_name).toString(),
                        Paths.get(running_dir, peer_id).toString());
            }
            while (searchForConnection) {
                IncomingConnectionHandler connHandler = new IncomingConnectionHandler(listening_socket.accept(), this.self_peer_id);
                connHandler.start();
            }
        } catch (Exception e) {
            // TODO: handle exception here
            e.printStackTrace();
        }
    }
}