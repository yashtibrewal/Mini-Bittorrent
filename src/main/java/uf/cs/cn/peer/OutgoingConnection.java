package uf.cs.cn.peer;

import uf.cs.cn.listeners.BitFieldEventListener;
import uf.cs.cn.message.*;
import uf.cs.cn.utils.BitFieldUtils;
import uf.cs.cn.utils.CommonConfigFileReader;
import uf.cs.cn.utils.HandShakeMessageUtils;
import uf.cs.cn.utils.PeerLogging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;

class OutgoingConnection extends Thread implements BitFieldEventListener {
    private PeerLogging peerLogging;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
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
        peerLogging = PeerLogging.getInstance();
    }

    public int getDestination_peer_id() {
        return destination_peer_id;
    }

    public void sendChokesAndUnChokes() {


        System.out.println("Calculating preferred neighbours");
        Peer.getInstance().calculatePreferredNeighbours();
        System.out.println("Resetting download counters");
        Peer.getInstance().resetDownloadCounters();

        Peer.getInstance().getPreferredNeighborsList().forEach((pN -> {
            Peer.getInstance().outgoingConnections.forEach((outgoingConnection -> {
                if (outgoingConnection.getDestination_peer_id() == pN) {
                    if (Peer.getInstance().getPreferredNeighborsList().contains(pN)) {
                        outgoingConnection.sendUnChokeMessages();
                    }else if(!Peer.getInstance().getPreferredNeighborsList().contains(pN))
                        outgoingConnection.sendChokeMessages();
                }
            }));
        }));
    }

    public void triggerPeriodicMessaging() throws InterruptedException {
        // update preferred neighbours
        // select one optimistically neighbour
        // send un choke message
        Peer.updateCloseConnection();
        System.out.println("Sending nothing presently");
        Thread.sleep(CommonConfigFileReader.un_chocking_interval* 1000L);
        sendChokesAndUnChokes();
    }


    public void run() {
        connection = null;
        try {
            connection = new Socket(destination_host_name, destination_port);
            objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
            objectInputStream = new ObjectInputStream(connection.getInputStream());
            Thread.sleep(1000);

            HandShakeMessageUtils.sendHandshake(objectOutputStream, handShakeMessage);
            HandShakeMessageUtils.receiveHandshake(objectInputStream);
            sendBitFieldMessage(objectOutputStream);


            while(HandShakeMessageUtils.recvCounter !=2 && HandShakeMessageUtils.sendCounter!=2) Thread.sleep(10);
            // starting the chokehandler
//            BUG: chokehandler does not work
//            ChokeHandler.getInstance();


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

    private void sendBitFieldMessage(ObjectOutputStream objectOutputStream) throws Exception {
        int numChunks = BitFieldUtils.getNumberOfChunks();
        int bitFieldSize = BitFieldUtils.getPayloadDataSize(numChunks);
        BitfieldMessage bitfieldMessage = new BitfieldMessage(bitFieldSize);
        byte[] output = bitfieldMessage.generatePayload();
        System.out.println("I am sending " + Arrays.toString(output));
        objectOutputStream.write(output);
        objectOutputStream.flush();
    }

    @Override
    public void sendInterestedMessages() {
        try {
            byte[] output = new InterestedMessage().getEncodedMessage();
            System.out.println("I am sending " + Arrays.toString(output));
            objectOutputStream.write(output);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotInterestedMessages() {
        try {
            byte[] output = new NotInterestedMessage().getEncodedMessage();
            System.out.println("I am sending " + Arrays.toString(output));
            objectOutputStream.write(output);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUnChokeMessages() {
        try {
            byte[] output = new UnChokeMessage().getEncodedMessage();
            System.out.println("I am sending " + Arrays.toString(output));
            objectOutputStream.write(output);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendChokeMessages() {
        try {
            byte[] output = new ChokeMessage().getEncodedMessage();
            System.out.println("I am sending " + Arrays.toString(output));
            objectOutputStream.write(output);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
