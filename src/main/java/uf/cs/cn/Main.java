package uf.cs.cn;

import uf.cs.cn.peer.Peer;

public class Main {

    public static void main(String[] args) {
        Peer server_instance = new Peer(true);
        server_instance.start();
        Peer client_instance = new Peer(false);
        client_instance.start();
    }
}