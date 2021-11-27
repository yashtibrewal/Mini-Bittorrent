package uf.cs.cn.peer;

import uf.cs.cn.message.BitfieldMessage;
import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.BitFieldUtils;
import uf.cs.cn.utils.HandShakeMessageUtils;
import uf.cs.cn.utils.PeerLogging;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

class OutgoingConnection extends Thread {
    private String destination_host_name;
    private Socket connection;
    private int destination_port;
    private int destination_peer_id;
    private int self_peer_id;
    private HandShakeMessage handShakeMessage;
    private PeerLogging peerLogging;

    public OutgoingConnection(String destination_host_name, int destination_port, int self_peer_id, int destination_peer_id) {
        this.destination_host_name = destination_host_name;
        this.self_peer_id = self_peer_id;
        this.destination_peer_id = destination_peer_id;
        this.destination_port = destination_port;
        handShakeMessage = new HandShakeMessage(this.self_peer_id);
        peerLogging = new PeerLogging();
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
                peerLogging.genericErrorLog("Invalid Handshake Message");
            }
            // Check if it's the actual peer_id
            if(!(new HandShakeMessage(handshakeMessageBuffer).checkPeerId(this.destination_peer_id))){
                peerLogging.genericErrorLog("Invalid Peer Id");
            }
            sendBitFieldMessage(objectOutputStream);
            // send infinitely
            while (true) {
                System.out.println("Sending nothing presently");
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
        int numChunks = BitFieldUtils.getNumberOfChunks();
        BitfieldMessage bitfieldMessage = new BitfieldMessage(numChunks);
        byte[] output = bitfieldMessage.generatePayload();
        System.out.println(Arrays.toString(output));
        objectOutputStream.write(output);
        objectOutputStream.flush();
    }

}
