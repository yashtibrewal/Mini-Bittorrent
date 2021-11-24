package uf.cs.cn.peer;

import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.HandShakeMessageUtils;
import uf.cs.cn.utils.MessageParser;
import uf.cs.cn.utils.PeerLogging;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IncomingConnectionHandler extends Thread {
    private static Socket connection;
    int self_peer_id;
    int client_peer_id;
    ObjectInputStream listening_stream = null;
    ObjectOutputStream speaking_stream = null;
    private PeerLogging peerLogging;
    private static MessageParser messageParser;

    private HandShakeMessage handShakeMessage;
    public IncomingConnectionHandler(Socket connection, int self_peer_id){
        this.connection = connection;
        this.self_peer_id = self_peer_id;
        handShakeMessage = new HandShakeMessage(self_peer_id);
        peerLogging = new PeerLogging(String.valueOf(self_peer_id));
        messageParser = new MessageParser();
    }

    public void run() {

        // handshake message reading
        byte handshake_32_byte_buffer[] = new byte[32];

        try {
            listening_stream = new ObjectInputStream(connection.getInputStream());
            speaking_stream = new ObjectOutputStream(connection.getOutputStream());

            // First message exchange is handshake
            // Handle handshake message
            listening_stream.read(handshake_32_byte_buffer);
            HandShakeMessageUtils.validateHandShakeMessage(handshake_32_byte_buffer);
            // Check if it's the actual peer_id
            HandShakeMessageUtils.validateHandShakeMessage(handshake_32_byte_buffer);

            // TODO: Ask faculty/TA how can server check the peer id
//            if(!(new HandShakeMessage(handshake_32_byte_buffer).checkPeerId(1000))){
//                throw new Exception("Invalid Peer Id");
//            }
            this.client_peer_id = new HandShakeMessage(handshake_32_byte_buffer).getPeerId();
            System.out.println("Received " + new String(handshake_32_byte_buffer) + " from the client peer "+this.client_peer_id);
            peerLogging.incomingTCPConnectionLog(String.valueOf(this.client_peer_id));
            // Send handshake
            speaking_stream.write(handShakeMessage.getBytes());
            System.out.println("Writing " + handShakeMessage.getMessage() + " to client peer " + this.client_peer_id);
            speaking_stream.flush();

            // listen infinitely
            while (true) {
                byte[] message_len = new byte[4];
                listening_stream.read(message_len, 0, 4);

                int message_len_val = 0;
                message_len_val = java.nio.ByteBuffer.wrap(message_len).getInt();
                byte[] actual_message = new byte[message_len_val];
                while(message_len_val-- > 0){
                    listening_stream.read();
                }



            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }



}
