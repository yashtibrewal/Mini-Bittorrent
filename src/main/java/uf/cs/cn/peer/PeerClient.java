package uf.cs.cn.peer;

import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.HandShakeMessageUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class PeerClient extends Thread {
    String hostName;
    private Socket connection;
    int server_id;
    int server_port;
    int peer_id;
    // Handshake message will be common for client and server
    private HandShakeMessage handShakeMessage;

    public PeerClient(String hostName,int server_port, int peer_id) {
        this.hostName = hostName;
        this.peer_id = peer_id;
        this.server_port = server_port;
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
        server_id = id;
    }

    public void run() {
        byte[] handshakeMessageBuffer = new byte[32];
        connection = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            connection = new Socket(hostName, server_port);
            objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
            objectInputStream = new ObjectInputStream(connection.getInputStream());

            // send handshake message
            System.out.println("Writing " + handShakeMessage.getMessage() + " to server");
            objectOutputStream.write(handShakeMessage.getEncodedMessage());
            objectOutputStream.flush();

            // receive handshake message
            objectInputStream.read(handshakeMessageBuffer);
            System.out.println("Receiver from client " + new String(handshakeMessageBuffer));
            HandShakeMessageUtils.parseHandshakeMessage(handshakeMessageBuffer);
            storePeerId(handshakeMessageBuffer);

            // send infinitely
            while (true) {
                objectOutputStream.write(1);
                objectOutputStream.flush();
                Thread.sleep(5000);
            }
        } catch (Exception ex) {
            System.err.println(ex.getCause() + " -Error encountered when sending data to remote server.");
            ex.printStackTrace();
        }finally {
            try {
                if(objectOutputStream!=null) {
                    objectOutputStream.close();
                }
                if (objectInputStream!=null){
                    objectInputStream.close();
                }
                if(connection!=null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println("Could not close connection due to " + e.getCause());
            }
        }
    }

}
