package uf.cs.cn.utils;

import uf.cs.cn.exceptions.InvalidMessageLengthException;
import uf.cs.cn.message.HandShakeMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * This class is used for checking the packet structure and raises exceptions accordingly
 */
public class HandShakeMessageUtils {
    static PeerLogging peerLogging = PeerLogging.getInstance();
    public static int recvCounter = 0;
    public static int sendCounter = 0;
    public static int incomingBitFieldCounter = 0;
    public static int outgoingBitfields = 0;

    // peerId is numeric
    public static boolean validatePeerId(byte[] message) throws Exception {
//        if (message.length < 32) {
            peerLogging.genericErrorLog("Invalid Peer Id");
//
//
//        }
        for (int i = message.length - 5; i < message.length; i++) {
            if (!Character.isDigit(message[i])) {
                return false;
            }
        }
        return true;
    }


    public static int receiveHandshake(ObjectInputStream ois) throws Exception {

        byte[] handshakeMessageBuffer = new byte[32];
        // receive handshake message
        ois.read(handshakeMessageBuffer);

        int destination_peer_id = new HandShakeMessage(handshakeMessageBuffer).getPeerId();
        System.out.println("Received " + Arrays.toString(handshakeMessageBuffer) + " from server peer " + destination_peer_id);
        recvCounter++;
//        if (!HandShakeMessageUtils.validateHandShakeMessage(handshakeMessageBuffer)) {
//            peerLogging.genericErrorLog("Invalid Handshake Message");
//        }

        // Check if it's the actual peer_id
//        if (!(new HandShakeMessage(handshakeMessageBuffer).checkPeerId(this.destination_peer_id))) {
//            peerLogging.genericErrorLog("Invalid Peer Id");
//        }
        return destination_peer_id;
    }

    public static boolean checkHandshakeHeaderMessage(byte[] message) throws Exception {
        if (message.length != 32) {
            throw new InvalidMessageLengthException();
        }
        // first 18 bytes is header
        char[] expected_header = new char[]{'P', '2', 'P', 'F', 'I', 'L', 'E', 'S', 'H', 'A', 'R', 'I', 'N', 'G', 'P', 'R', 'O', 'J'};
        for (int i = 0; i < 18; i++) {
            if (message[i] != expected_header[i]) return false;
        }
        return true;
    }

    public static void sendHandshake(ObjectOutputStream oos, HandShakeMessage handShakeMessage) throws IOException {
        // send handshake message
        System.out.println("Sending handshake message which is "+Arrays.toString(handShakeMessage.getBytes()));
        oos.write(handShakeMessage.getBytes());
        oos.flush();
        sendCounter++;
    }

    public static boolean checkHandshakePaddingMessage(byte[] message) throws Exception {
        if (message.length != 32) {
            throw new InvalidMessageLengthException();
        }
        for (int i = 18; i < 28; i++) {
            if (message[i] != '0') return false;
        }
        return true;
    }

    public static boolean validateHandShakeMessage(byte[] message) {
        try {
            // assumption the message is 32 bytes
            // check for message header
            checkHandshakeHeaderMessage(message);
            // check for 8 bytes of 0s
            checkHandshakePaddingMessage(message);
            // check peer ids at the last
            validatePeerId(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
