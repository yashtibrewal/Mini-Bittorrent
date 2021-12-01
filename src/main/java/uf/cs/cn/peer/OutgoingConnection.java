package uf.cs.cn.peer;

import uf.cs.cn.listeners.BitFieldEventListener;
import uf.cs.cn.message.*;
import uf.cs.cn.utils.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static uf.cs.cn.utils.FileMerger.deleteChunks;

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

    synchronized public void sendChokesAndUnChokes() {

        System.out.println("Calculating preferred neighbours");
        Peer.getInstance().calculatePreferredNeighbours();
        System.out.println("-PREFERRED NEIGHBOURS are - " + Peer.preferredNeighborsList);
        Peer.getInstance().resetDownloadCounters();
        System.out.println("-PRIORITY QUEUE is - " + Peer.getInstance().priorityQueue);
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


            Thread.sleep(CommonConfigFileReader.un_chocking_interval*1000L);
//            while(HandShakeMessageUtils.getRecvCounter() != PeerInfoConfigFileReader.numberOfPeers-1
//                    && HandShakeMessageUtils.getSendCounter()!= PeerInfoConfigFileReader.numberOfPeers-1) Thread.sleep(10);



            sendBitFieldMessage(objectOutputStream);

            Thread.sleep(CommonConfigFileReader.un_chocking_interval*1000L);

            while(HandShakeMessageUtils.getOutgoingBitfields() != PeerInfoConfigFileReader.numberOfPeers-1
            && HandShakeMessageUtils.getIncomingBitFieldCounter() != PeerInfoConfigFileReader.numberOfPeers-1) Thread.sleep(10);


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
            deleteChunks();
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
        for(byte b:output){
            objectOutputStream.write(b);
        }
        objectOutputStream.flush();
        HandShakeMessageUtils.setOutgoingBitfields(HandShakeMessageUtils.getOutgoingBitfields()+1);
    }

    @Override
    synchronized public void sendInterestedMessages() {
        try {
            byte[] output = new InterestedMessage().getEncodedMessage();
            for(byte b:output){
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
            for(byte b:output){
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
            for(byte b:output){
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
            for(byte b:output){
                objectOutputStream.write(b);
            }
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
