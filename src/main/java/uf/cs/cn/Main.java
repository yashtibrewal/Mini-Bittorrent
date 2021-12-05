package uf.cs.cn;

import uf.cs.cn.peer.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter the peer id");
            args = new String[]{new BufferedReader(new InputStreamReader(System.in)).readLine()};
            // TODO: pass the peer_id to constructor from Command Line Argument
        }
        int peer_id;
        try {
            peer_id = extractPeerId(args);
            Peer peer = Peer.getInstance(peer_id);
            peer.start();
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

    }

    public static int extractPeerId(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Invalid command line arguments passed");
        }
        return Integer.parseInt(args[0]);
    }
}
