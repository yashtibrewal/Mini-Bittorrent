package uf.cs.cn.peer;

import uf.cs.cn.listeners.BitFieldEventListener;
import uf.cs.cn.message.*;
import uf.cs.cn.utils.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class OutgoingConnection extends Thread implements BitFieldEventListener {
    private final String destination_host_name;
    private final int destination_port;
    private final int destination_peer_id;
    private final int self_peer_id;
    private final HandShakeMessage handShakeMessage;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    private Socket connection;
    public OutgoingConnection(String destination_host_name, int destination_port, int self_peer_id, int destination_peer_id) {
        this.destination_host_name = destination_host_name;
        this.self_peer_id = self_peer_id;
        this.destination_peer_id = destination_peer_id;
        this.destination_port = destination_port;
        handShakeMessage = new HandShakeMessage(this.self_peer_id);
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public int getDestination_peer_id() {
        return destination_peer_id;
    }

    public void triggerPeriodicMessaging() throws InterruptedException {
        // update preferred neighbours
        // select one optimistically neighbour
        // send un choke message
        Peer.updateCloseConnection();
        Thread.sleep(CommonConfigFileReader.un_chocking_interval * 1000L);
        MessageSender.sendChokesAndUnChokes();
    }

    private boolean waitingForConnection() {
        try {
            System.out.println("Waiting for connection");
            connection = new Socket(destination_host_name, destination_port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void run() {
        try {
            Thread.sleep(2000);
            while (!waitingForConnection()) ;
            objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
            objectInputStream = new ObjectInputStream(connection.getInputStream());
            Thread.sleep(1000);

            HandShakeMessageUtils.sendHandshake(objectOutputStream, handShakeMessage);
            HandShakeMessageUtils.receiveHandshake(objectInputStream);

            Thread.sleep(CommonConfigFileReader.un_chocking_interval * 1000L);
            sendBitFieldMessage(objectOutputStream);

            Thread.sleep(CommonConfigFileReader.un_chocking_interval * 1000L);
            while (HandShakeMessageUtils.getOutgoingBitfields() != PeerInfoConfigFileReader.numberOfPeers - 1
                    && HandShakeMessageUtils.getIncomingBitFieldCounter() != PeerInfoConfigFileReader.numberOfPeers - 1)
                Thread.sleep(10);

            // send infinitely
            while (!Peer.isClose_connection()) {
                triggerPeriodicMessaging();
            }

            objectOutputStream.close();
            objectInputStream.close();
            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getCause() + " -Error encountered when sending data to remote server.");
            ex.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println("Could not close connection due to " + e.getCause());
            }
        }
    }


    synchronized private void sendBitFieldMessage(ObjectOutputStream objectOutputStream) throws Exception {
        int numChunks = BitFieldUtils.getNumberOfChunks();
        int bitFieldSize = BitFieldUtils.getPayloadDataSize(numChunks);
        BitfieldMessage bitfieldMessage = new BitfieldMessage(bitFieldSize);
        byte[] output = bitfieldMessage.generatePayload();
        for (byte b : output) {
            objectOutputStream.write(b);
        }
        objectOutputStream.flush();
        HandShakeMessageUtils.setOutgoingBitfields(HandShakeMessageUtils.getOutgoingBitfields() + 1);
    }

    @Override
    synchronized public void sendInterestedMessages() {
        try {
            byte[] output = new InterestedMessage().getEncodedMessage();
            for (byte b : output) {
                objectOutputStream.write(b);
            }
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    synchronized public void sendNotInterestedMessages() {
        try {
            byte[] output = new NotInterestedMessage().getEncodedMessage();
            for (byte b : output) {
                objectOutputStream.write(b);
            }
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public void sendUnChokeMessages() {
        try {
            byte[] output = new UnChokeMessage().getEncodedMessage();
            for (byte b : output) {
                objectOutputStream.write(b);
            }
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public void sendChokeMessages() {
        try {
            byte[] output = new ChokeMessage().getEncodedMessage();
            for (byte b : output) {
                objectOutputStream.write(b);
            }
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
