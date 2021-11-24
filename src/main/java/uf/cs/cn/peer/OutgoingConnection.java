package uf.cs.cn.peer;

import uf.cs.cn.message.BitfieldMessage;
import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.BitFieldUtils;
import uf.cs.cn.utils.HandShakeMessageUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class OutgoingConnection extends Thread {
    private String destination_host_name;
    private Socket connection;
    private int destination_port;
    private int destination_peer_id;
    private int self_peer_id;
    private HandShakeMessage handShakeMessage;

    public OutgoingConnection(String destination_host_name, int destination_port, int self_peer_id, int destination_peer_id) {
        this.destination_host_name = destination_host_name;
        this.self_peer_id = self_peer_id;
        this.destination_peer_id = destination_peer_id;
        this.destination_port = destination_port;
        handShakeMessage = new HandShakeMessage(this.self_peer_id);
    }

    public void run() {
        byte[] handshakeMessageBuffer = new byte[32];
        connection = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            connection = new Socket(destination_host_name, destination_port);
            objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
            objectInputStream = new ObjectInputStream(connection.getInputStream());

            // send handshake message
            System.out.println("Writing " + handShakeMessage.getMessage() + " to server peer " + destination_peer_id);
            objectOutputStream.write(handShakeMessage.getBytes());
            objectOutputStream.flush();

            // receive handshake message
            objectInputStream.read(handshakeMessageBuffer);
            System.out.println("Received " + new String(handshakeMessageBuffer) + " from server peer " + destination_peer_id);
            if(!HandShakeMessageUtils.validateHandShakeMessage(handshakeMessageBuffer)){
                throw new Exception("Invalid Handshake Message");
            }
            // Check if it's the actual peer_id
            if(!(new HandShakeMessage(handshakeMessageBuffer).checkPeerId(this.destination_peer_id))){
                throw new Exception("Invalid Peer Id");
            }


            sendBitFieldMessage(objectOutputStream);

            // send infinitely
            while (true) {
                System.out.println("Have Messages");
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

    private void sendBitFieldMessage(ObjectOutputStream objectOutputStream) throws Exception {
        int numChunks = BitFieldUtils.getNumberOfChunks(this.self_peer_id);
        int messageLength = 0;

        if (numChunks%8 == 0) {
            messageLength = numChunks%8;
        } else {
            messageLength = numChunks%8 + 1;
        }

        BitfieldMessage bitfieldMessage = new BitfieldMessage( messageLength, this.self_peer_id);
        byte payload = bitfieldMessage.generatePayload();

        objectOutputStream.write(payload);
        objectOutputStream.flush();
    }

}
