package uf.cs.cn;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

public class PeerAdmin {
    private String peerID;
    private int portionCount;
    private PeerServer server;
    private Thread serverThread;
    private volatile ServerSocket listener;
    private volatile PeerLogger logger;
    private volatile boolean completion = false;

    private CommonConfig commonConfig = new CommonConfig();
    private PeerInfoConfig peerInfoConfig= new PeerInfoConfig();

    private volatile String[] requestedInfo;
    private ArrayList<String> peerList = new ArrayList<>();

    private volatile String optimisticallyUnchokedPeer;
    private RemotePeerInfo remotePeerInfo;
    private volatile RandomAccessFile randomAccessFile;
    private volatile ChokeHandler chokeHandler;
    private volatile OptimisticUnchokeHandler optimisticUnchokeHandler;
    private volatile TerminationHandler terminationHandler;

    private HashMap<String, RemotePeerInfo> peerInfoMapping = new HashMap<>();
    private volatile HashMap<String, PeerHandler> activePeers = new HashMap<>();
    private volatile HashMap<String, Thread> joinedThreads = new HashMap<>();
    private volatile HashMap<String, BitSet> portionBinaryInfo = new HashMap<>();
    private volatile HashMap<String, Integer> downloadRate = new HashMap<>();
    private volatile HashSet<String> unChokedList = new HashSet<>();
    private volatile HashSet<String> interestedList = new HashSet<>();

    public PeerAdmin(String peerID) {
        this.peerID = peerID;
        this.logger = new PeerLogger(peerID);
        this.initPeer();
        this.optimisticUnchokeHandler = new OptimisticUnchokeHandler(this);
        this.terminationHandler = new TerminationHandler(this);
        this.chokeHandler.startJob();
        this.optimisticUnchokeHandler.startJob();
    }

    public void initPeer() {
        try {
            this.commonConfig.loadCommonFile();
            this.peerInfoConfig.loadConfigFile();
            this.portionCount = this.calcPieceCount();
            this.requestedInfo = new String[this.portionCount];
            this.remotePeerInfo = this.peerInfoConfig.getPeerConfig(this.peerID);
            this.peerInfoMapping = this.peerInfoConfig.getPeerInfoMap();
            this.peerList = this.peerInfoConfig.getPeerList();
            String filepath = "peer_" + this.peerID;
            File file = new File(filepath);
            file.mkdir();
            String filename = filepath + "/" + getFileName();
            file = new File(filename);
            if (!hasFile()) {
                file.createNewFile();
            }
            this.randomAccessFile = new RandomAccessFile(file, "rw");
            if (!hasFile()) {
                this.randomAccessFile.setLength(this.getFileSize());
            }
            this.initializePieceAvailability();
            this.startServer();
            this.createNeighbourConnections();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            this.listener = new ServerSocket(this.remotePeerInfo.peerPort);
            this.server = new PeerServer(this.peerID, this.listener, this);
            this.serverThread = new Thread(this.server);
            this.serverThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNeighbourConnections() {
        try {
            Thread.sleep(3000);
            for (String pid : this.peerList) {
                if (pid.equals(this.peerID)) {
                    break;
                }
                else {
                    RemotePeerInfo peer = this.peerInfoMapping.get(pid);
                    Socket temp = new Socket(peer.peerAddress, peer.peerPort);
                    PeerHandler p = new PeerHandler(temp, this);
                    p.setEndPeerID(pid);
                    this.addJoinedPeer(p, pid);
                    Thread t = new Thread(p);
                    this.addJoinedThreads(pid, t);
                    t.start();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializePieceAvailability() {
        for (String pid : this.peerInfoMapping.keySet()) {
            BitSet availability = new BitSet(this.portionCount);
            if (this.peerInfoMapping.get(pid).containsFile) {
                availability.set(0, this.portionCount);
                this.portionBinaryInfo.put(pid, availability);
            }
            else {
                availability.clear();
                this.portionBinaryInfo.put(pid, availability);
            }
        }
    }

    public synchronized byte[] readFromFile(int portionIndex) {
        try {
            int position = this.getPieceSize() * portionIndex;
            int size = this.getPieceSize();
            if (portionIndex == getPortionCount() - 1) {
                size = this.getFileSize() % this.getPieceSize();
            }
            this.randomAccessFile.seek(position);
            byte[] data = new byte[size];
            this.randomAccessFile.read(data);
            return data;
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return new byte[0];
    }

    public synchronized void writeToFile(byte[] data, int pieceindex) {
        try {
            int position = this.getPieceSize() * pieceindex;
            this.randomAccessFile.seek(position);
            this.randomAccessFile.write(data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void updatePieceAvailability(String peerID, int index) {
        this.portionBinaryInfo.get(peerID).set(index);
    }

    public synchronized void addJoinedPeer(PeerHandler p, String endpeerid) {
        this.activePeers.put(endpeerid, p);
    }

    public synchronized void addJoinedThreads(String epeerid, Thread th) {
        this.joinedThreads.put(epeerid, th);
    }

    public synchronized HashMap<String, Thread> getJoinedThreads() {
        return this.joinedThreads;
    }

    public PeerHandler getPeerHandler(String peerid) {
        return this.activePeers.get(peerid);
    }

    public BitSet getAvailabilityOf(String pid) {
        return this.portionBinaryInfo.get(pid);
    }

    public synchronized boolean checkIfInterested(String endpeerid) {
        BitSet end = this.getAvailabilityOf(endpeerid);
        BitSet mine = this.getAvailabilityOf(this.peerID);
        for (int i = 0; i < end.size() && i < this.portionCount; i++) {
            if (end.get(i) && !mine.get(i)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void setRequestedInfo(int id, String peerID) {
        this.requestedInfo[id] = peerID;
    }

    public synchronized int checkForRequested(String endpeerid) {
        BitSet end = this.getAvailabilityOf(endpeerid);
        BitSet mine = this.getAvailabilityOf(this.peerID);
        for (int i = 0; i < end.size() && i < this.portionCount; i++) {
            if (end.get(i) && !mine.get(i) && this.requestedInfo[i] == null) {
                setRequestedInfo(i, endpeerid);
                return i;
            }
        }
        return -1;
    }

    public synchronized void resetRequested(String endpeerid) {
        for (int i = 0; i < this.requestedInfo.length; i++) {
            if (this.requestedInfo[i] != null && this.requestedInfo[i].compareTo(endpeerid) == 0) {
                setRequestedInfo(i, null);
            }
        }
    }

    public String getPeerID() {
        return this.peerID;
    }

    public PeerLogger getLogger() {
        return this.logger;
    }

    public boolean hasFile() {
        return this.remotePeerInfo.containsFile ;
    }

    public int getNoOfPreferredNeighbors() {
        return this.commonConfig.numberOfPreferredNeighbors;
    }

    public int getUnchockingInterval() {
        return this.commonConfig.unchokingInterval;
    }

    public int getOptimisticUnchockingInterval() {
        return this.commonConfig.optimisticUnchokingInterval;
    }

    public String getFileName() {
        return this.commonConfig.fileName;
    }

    public int getFileSize() {
        return this.commonConfig.fileSize;
    }

    public int getPieceSize() {
        return this.commonConfig.portionSize;
    }

    public int calcPieceCount() {
        int len = getFileSize() / getPieceSize();
        if (getFileSize() % getPieceSize() != 0) {
            len++;
        }
        return len;
    }

    public int getPortionCount() {
        return this.portionCount;
    }

    public int getCompletedPieceCount() {
        return this.portionBinaryInfo.get(this.peerID).cardinality();
    }

    public synchronized void addToInterestedList(String endPeerId) {
        this.interestedList.add(endPeerId);
    }

    public synchronized void removeFromInterestedList(String endPeerId) {
        if (this.interestedList != null) {
            this.interestedList.remove(endPeerId);
        }
    }

    public synchronized void resetInterestedList() {
        this.interestedList.clear();
    }
    public synchronized void resetUnchokedList() {
        this.unChokedList.clear();
    }
    public synchronized HashSet<String> getInterestedList() {
        return this.interestedList;
    }
    public synchronized boolean addUnchokedPeer(String peerid) {
        return this.unChokedList.add(peerid);
    }
    public synchronized HashSet<String> getUnchokedList() {
        return this.unChokedList;
    }
    public synchronized void updateUnchokedList(HashSet<String> newSet) {
        this.unChokedList = newSet;
    }

    public synchronized void setOptimisticallyUnchokedPeer(String peerid) {
        this.optimisticallyUnchokedPeer = peerid;
    }
    public synchronized String getOptimisticallyUnchokedPeer() {
        return this.optimisticallyUnchokedPeer;
    }

    public synchronized boolean checkIfAllPeersHaveTheFile() {
        for (String peer : this.portionBinaryInfo.keySet()) {
            if (this.portionBinaryInfo.get(peer).cardinality() != this.portionCount) {
                return false;
            }
        }
        return true;
    }
}
