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
    private final PeerLogging peerLogging;
    private final HandShakeMessage handShakeMessage;
    int self_peer_id;
    int client_peer_id;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    int message_len_val;
    byte[] message_len_arr;
    byte[] actual_message_without_len;

    public IncomingConnectionHandler(Socket connection, int self_peer_id) {
        this.connection = connection;
        this.self_peer_id = self_peer_id;
        handShakeMessage = new HandShakeMessage(self_peer_id);
        peerLogging = PeerLogging.getInstance();
    }

    public void listenAndParseActualMessage() throws IOException {
        // memory for reading message length header
        message_len_arr = new byte[4];
        // reading the message length header
        for (int i = 0; i < message_len_arr.length; i++) {
            message_len_arr[i] = (byte) inputStream.read();
        }
        // converting to readable int
        message_len_val = new BigInteger(message_len_arr).intValue();
        // breaking at the end of the stream
        if (message_len_val == -1) {
            return;
        }
        // memory declaration for reading the payload
        actual_message_without_len = new byte[message_len_val];

        for (int i = 0; i < actual_message_without_len.length; i++) {
            actual_message_without_len[i] = (byte) inputStream.read();
        }
        // parsing the payload
        MessageParser.parse(new ActualMessage(message_len_arr, actual_message_without_len), client_peer_id);
    }

    public void run() {

        // handshake message reading

        try {
            inputStream = new ObjectInputStream(connection.getInputStream());
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            Thread.sleep(1000);

            //receive handshake
            this.client_peer_id = HandShakeMessageUtils.receiveHandshake(inputStream);
            //sending handshake
            HandShakeMessageUtils.sendHandshake(outputStream, handShakeMessage);

            // Wait for syncing all peers to common point
            Thread.sleep(CommonConfigFileReader.un_chocking_interval * 1000L);

            // Wait for the outgoing thread to send out the bit field messages.
            while (HandShakeMessageUtils.getOutgoingBitfields() != PeerInfoConfigFileReader.numberOfPeers - 1)
                Thread.sleep(10);

            // Single read for bit field message read.
            listenAndParseActualMessage();

            Thread.sleep(CommonConfigFileReader.un_chocking_interval * 1000L);
            // listen infinitely
            while (!Peer.isClose_connection()) {
                listenAndParseActualMessage();
            }

            peerLogging.closeLogger();
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        } finally {
            deleteChunks();
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
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
