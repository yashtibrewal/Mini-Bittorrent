package uf.cs.cn.utils;

import uf.cs.cn.exceptions.InvalidMessageLengthException;

/**
 * This class is used for checking the packet structure and raises exceptions accordingly
 */
public class HandShakeMessageUtils {
    static PeerLogging peerLogging = PeerLogging.getInstance();

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
