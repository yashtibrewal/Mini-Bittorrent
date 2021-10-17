package uf.cs.cn.utils;

/**
 * This class is used for checking the packet structure and raises exceptions accordingly
 */
public class HandShakeMessageUtils {

    // TODO: update length checks for all functions to 32 bytes
    // TODO: Creating a separate Exception for Handshake Message errors, If needed, create a common for all message errors

    // peerId is numeric
    public static boolean checkPeerId(byte[] message) throws Exception {
        if (message.length < 32) {
            throw new Exception("Invalid Peer Id");
        }
        for (int i=message.length-5; i< message.length; i++) {
            if (!Character.isDigit(message[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkHandshakeHeaderMessage(byte[] message)throws Exception {
        if(message.length < 18) {
            throw new Exception("Invalid Header Message");
        }
        // first 18 bytes is header
        char[] expected_header =  new char[]{'P','2','P','F','I','L','E','S','H','A','R','I','N','G','P','R','O','J'};
        for(int i=0;i<18;i++) {
            if(message[i] != expected_header[i]) return false;
        }
        return true;
    }

    public static boolean checkHandshakePaddingMessage (byte[] message) throws Exception{
        if(message.length < 28){
            throw new Exception("Invalid Padding Id");
        }
        for(int i=18;i<28;i++){
            if(message[i] != '0') return false;
        }
        return true;
    }

    public static void parseHandshakeMessage(byte[] message) throws Exception {
        // assumption the message is 32 bytes
        // check for message header
        checkHandshakeHeaderMessage(message);
        // check for 8 bytes of 0s
        checkHandshakePaddingMessage(message);
        // check peer ids at the last
        checkPeerId(message);
    }
}
