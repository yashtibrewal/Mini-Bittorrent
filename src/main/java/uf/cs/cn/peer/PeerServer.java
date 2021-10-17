package uf.cs.cn.peer;

import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.HandShakeMessageUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer extends Thread{
    private ServerSocket listening_socket;
    private Socket connection;
    private int client_id;
    private HandShakeMessage handShakeMessage;
    private int server_port;
    private int peer_id;

    public PeerServer(int server_port, int peer_id){
        this.server_port = server_port;
        this.peer_id = peer_id;
        handShakeMessage = new HandShakeMessage(peer_id);
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
        byte handshake_32_byte_buffer[] = new byte[32]; // handshake message reading
        listening_socket = null;
        ObjectInputStream listening_stream = null;
        ObjectOutputStream speaking_stream = null;
        try {
            listening_socket = new ServerSocket(server_port);
            connection = listening_socket.accept();
            listening_stream = new ObjectInputStream(connection.getInputStream());
            speaking_stream = new ObjectOutputStream(connection.getOutputStream());
            // First message exchange is handshake
            // Handle handshake message
            listening_stream.read(handshake_32_byte_buffer);
            System.out.println("Receiver from client " + new String(handshake_32_byte_buffer));
            HandShakeMessageUtils.parseHandshakeMessage(handshake_32_byte_buffer);
            storePeerId(handshake_32_byte_buffer);

            // Send handshake
            speaking_stream.write(handShakeMessage.getEncodedMessage());
            System.out.println("Writing " + handShakeMessage.getMessage() + " to client");
            speaking_stream.flush();

            // listen infinitely
            while(true) {
                System.out.print(listening_stream.read());
            }

            // handle common message
        }catch (Exception e) {
            // TODO: handle
            e.printStackTrace();
        } finally {
            try {
                if(listening_stream!=null)
                    listening_stream.close();
                if(speaking_stream!=null)
                    listening_stream.close();
                if(listening_socket!=null)
                    listening_stream.close();
            } catch (Exception e) {
                System.out.println("Could not release resources due to " + e.getCause());
            }
        }

    }
}