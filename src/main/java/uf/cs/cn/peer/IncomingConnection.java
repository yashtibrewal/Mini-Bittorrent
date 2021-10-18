package uf.cs.cn.peer;

import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.HandShakeMessageUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class IncomingConnection extends Thread{
    private ServerSocket listening_socket;
    private Socket connection;
    private int client_id;
    private int server_port;
    private int peer_id;
    private ArrayList<Integer> destination_peer_ids;

    public IncomingConnection(int server_port, int peer_id){
        this.server_port = server_port;
        this.peer_id = peer_id;
    }

    private void storePeerId(byte[] message) throws Exception {
        // extracting the last 4 characters and converting to integer and storing it
        if(message.length != 32) {
            throw new Exception("Invalid Header Message");
        }
        int id = 0;
        for(int i=28;i<32;i++) {
            // check for 0 to 9 char range
            id *= (message[i] - 48);
        }
        client_id = id;
    }


    public void run() {
        listening_socket = null;
        boolean searchForConnection = true;
        try {
            listening_socket = new ServerSocket(server_port);
            while (searchForConnection) {
                connection = listening_socket.accept();
                IncomingConnectionHandler connHandler = new IncomingConnectionHandler(connection, this.peer_id);
                connHandler.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }
}