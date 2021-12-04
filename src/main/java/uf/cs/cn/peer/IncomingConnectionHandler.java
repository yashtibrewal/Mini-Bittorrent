package uf.cs.cn.peer;

import uf.cs.cn.message.ActualMessage;
import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;

import static uf.cs.cn.utils.FileMerger.deleteChunks;

public class IncomingConnectionHandler extends Thread {
    private final Socket connection;
    int self_peer_id;
    int client_peer_id;
    ObjectInputStream listening_stream;
    ObjectOutputStream speaking_stream;
    private final PeerLogging peerLogging;

    int message_len_val, bytes_read_from_stream;
    byte[] message_len_arr;
    byte[] actual_message_without_len;
    private final HandShakeMessage handShakeMessage;

    public IncomingConnectionHandler(Socket connection, int self_peer_id) {
        this.connection = connection;
        this.self_peer_id = self_peer_id;
        handShakeMessage = new HandShakeMessage(self_peer_id);
        peerLogging = PeerLogging.getInstance();
    }

    public void listenMessage() throws IOException {
        // memory for reading message length header
        message_len_arr = new byte[4];

        // reading the message length header
        for(int i=0;i<message_len_arr.length;i++){
            message_len_arr[i] = (byte) listening_stream.read();
        }
        // converting to readable int
        message_len_val = new BigInteger(message_len_arr).intValue();

        // breaking at the end of the stream
        if(message_len_val == -1) {
            return;
        }

        // memory declaration for reading the payload
        actual_message_without_len = new byte[message_len_val];

        for(int i=0;i<actual_message_without_len.length;i++){
            actual_message_without_len[i]= (byte) listening_stream.read();
        }

        // parsing the payload
        MessageParser.parse(new ActualMessage(message_len_arr, actual_message_without_len), client_peer_id);


    }

    public void run() {

        // handshake message reading

        try {
            listening_stream = new ObjectInputStream(connection.getInputStream());
            speaking_stream = new ObjectOutputStream(connection.getOutputStream());
            Thread.sleep(1000);
            // Check if it's the actual peer_id
            // TODO: Ask faculty/TA how can server check the peer id
//            if(!(new HandShakeMessage(handshake_32_byte_buffer).checkPeerId(1000))){
//                peerLogging.genericErrorLog("Invalid Peer Id");
//            }

            //recv handshake
            this.client_peer_id = HandShakeMessageUtils.receiveHandshake(listening_stream);
            //sending handshake
            HandShakeMessageUtils.sendHandshake(speaking_stream, handShakeMessage);

//            while(HandShakeMessageUtils.getRecvCounter() != PeerInfoConfigFileReader.numberOfPeers-1 && HandShakeMessageUtils.getSendCounter()!= PeerInfoConfigFileReader.numberOfPeers-1) Thread.sleep(10);

            Thread.sleep(CommonConfigFileReader.un_chocking_interval*1000L);
            //listen to bitfield message first
            while(HandShakeMessageUtils.getOutgoingBitfields() != PeerInfoConfigFileReader.numberOfPeers-1
                    && HandShakeMessageUtils.getIncomingBitFieldCounter() != PeerInfoConfigFileReader.numberOfPeers-1) Thread.sleep(10);
            listenMessage();

            Thread.sleep(CommonConfigFileReader.un_chocking_interval*1000L);
            // listen infinitely
            while (!Peer.isClose_connection()) {
                listenMessage();
            }

            listening_stream.close();
            speaking_stream.close();
            connection.close();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        } finally {

            deleteChunks();
            try {
                if (speaking_stream != null) {
                    speaking_stream.close();
                }
                if (listening_stream != null) {
                    listening_stream.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println("Could not close connection due to " + e.getCause());
            }
        }
    }
}
