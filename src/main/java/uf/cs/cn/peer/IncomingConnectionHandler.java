package uf.cs.cn.peer;

import uf.cs.cn.message.ActualMessage;
import uf.cs.cn.message.HandShakeMessage;
import uf.cs.cn.utils.HandShakeMessageUtils;
import uf.cs.cn.utils.MessageParser;
import uf.cs.cn.utils.PeerInfoConfigFileReader;
import uf.cs.cn.utils.PeerLogging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;

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
        bytes_read_from_stream = listening_stream.read(message_len_arr);
        System.out.println("Bytes read from the stream : "+ bytes_read_from_stream);
        System.out.println(Arrays.toString(message_len_arr));

        // converting to readable int
        message_len_val = new BigInteger(message_len_arr).intValue();
        System.out.println("Will read these many bytes more : "+ message_len_val);


        // memory declaration for reading the payload
        actual_message_without_len = new byte[message_len_val];

        System.out.println(Arrays.toString(actual_message_without_len));
        // reading the payload ( with type )
        bytes_read_from_stream = listening_stream.read(actual_message_without_len);
        System.out.println("Bytes read from the stream : "+ bytes_read_from_stream);

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

            while(HandShakeMessageUtils.recvCounter != PeerInfoConfigFileReader.numberOfPeers && HandShakeMessageUtils.sendCounter!= PeerInfoConfigFileReader.numberOfPeers) Thread.sleep(10);

            //listen to bitfield message first
            listenMessage();
            while(HandShakeMessageUtils.outgoingBitfields != PeerInfoConfigFileReader.numberOfPeers-1
                    && HandShakeMessageUtils.bitfieldCounter != PeerInfoConfigFileReader.numberOfPeers-1) Thread.sleep(10);

            // listen infinitely
            while (!Peer.isClose_connection()) {
                listenMessage();
            }

            ChokeHandler.cancelJob();
            listening_stream.close();
            speaking_stream.close();
            connection.close();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        } finally {
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
