package uf.cs.cn.peer;

import uf.cs.cn.utils.CommonConfigFileReader;
import uf.cs.cn.utils.FileSplitter;
import uf.cs.cn.utils.PeerInfoConfigFileReader;
import uf.cs.cn.utils.PeerLogging;

import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Paths;

public class PeerServer extends Thread {
    private final int self_port;
    private final int self_peer_id;
    private ServerSocket serverSocket;

    public PeerServer(int self_port, int self_peer_id) {
        this.self_port = self_port;
        this.self_peer_id = self_peer_id;
    }

    /**
     * We split the file into the pieces.
     */
    private void splitFileIntoChunks() {
        System.out.println("Splitting the file in pieces.");
        String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
        String peer_id = String.valueOf(self_peer_id);
        FileSplitter.splitFile(
                Paths.get(running_dir, peer_id, CommonConfigFileReader.file_name).toString(),
                Paths.get(running_dir, peer_id).toString());
    }

    public void run() {
        serverSocket = null;
        try {
            /**
             * Starting the server at the {@link PeerServer#self_port}.
             * Server means a connection which is duplex and waiting for the other peer to get connected.
             */
            serverSocket = new ServerSocket(self_port);
            System.out.println("Peer " + self_peer_id + " running server socket on " + self_port);
            boolean is_server = false;
            for (PeerInfoConfigFileReader.PeerInfo peerInfo : PeerInfoConfigFileReader.getPeerInfoList()) {
                /**
                 * This line will only iterate all elements until it reaches itself. Point of doing this is to connect
                 * only to the peers who are already in the network. Assuming we run the peers as per their index numbers
                 * mentioned in the file in sequential order, we are connecting to the ones who are already running.
                 */
                if (Peer.getPeerId() == peerInfo.getPeer_id() && peerInfo.isHas_file()) {
                    is_server = true;
                }
            }
            if (is_server) {
                splitFileIntoChunks();
            }

            /**
             * We wait for a total of n-1 connections, as there are n peers in the networks, we wait for each peer other
             * then us to connect to us.
             */
            int counter = 0;
            while (counter != PeerInfoConfigFileReader.getPeerInfoList().size() - 1) {
                IncomingConnectionHandler connHandler = new IncomingConnectionHandler(serverSocket.accept(), this.self_peer_id);
                int port = 0;
                URL url = connHandler.getContextClassLoader().getResource(connHandler.getName());
                if (url != null)
                    System.out.println("Incoming connection from port " + port);
                logIncomingConnection(port);
                connHandler.start();
                counter++;
            }
        } catch (Exception e) {
            // TODO: handle exception here
            e.printStackTrace();
        }
    }

    private void logIncomingConnection(int port) {
        for (PeerInfoConfigFileReader.PeerInfo peerInfo : PeerInfoConfigFileReader.getPeerInfoList()) {
            if (peerInfo.getListening_port() == port) {
                PeerLogging.getInstance().incomingTCPConnectionLog(peerInfo.getPeer_host_name());
            }
            break;
        }
    }
}