package uf.cs.cn;

import uf.cs.cn.peer.Peer;

public class Main {

    public static void main(String[] args) {
        // pass the peer_id to constructor from CLA
        int peer_id;
        try {
            peer_id = extractPeerId(args);
            Peer server_instance = new Peer(true, peer_id);
            server_instance.start();
            Peer client_instance = new Peer(false, peer_id);
            client_instance.start();
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

    }

    public static int extractPeerId(String[] args) throws Exception {
        if (args.length == 0 || args.length > 1) {
            throw new Exception("Invalid command line arguments passed");
        }
        return Integer.parseInt(args[0]);
    }
}
