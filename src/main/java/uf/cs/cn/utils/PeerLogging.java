package uf.cs.cn.utils;

import uf.cs.cn.peer.Peer;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: convert to single ton
public class PeerLogging {
    private static PeerLogging peerLogging;
    private String logFileName;
    private final String peerId;
    private FileHandler peerLogFileHandler;
    private SimpleDateFormat dateFormat = null;
    private Logger peerLogger;

    private PeerLogging() {
        this.peerId = Peer.getPeerId() + "";
        startLogger();
    }

    public static PeerLogging getInstance() {
        if (PeerLogging.peerLogging == null) {
            peerLogging = new PeerLogging();
        }
        return peerLogging;
    }

    public void startLogger() {
        try {
            this.dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            this.logFileName = "log_" + this.peerId + ".log";
            // TODO: if folder path does not exists, create it.
            this.peerLogFileHandler = new FileHandler(Paths.get(System.getProperty("user.dir"),
                    String.valueOf(this.peerId),
                    this.logFileName).toString(),
                    false);
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
            this.peerLogFileHandler.setFormatter(new SimpleFormatter());
            this.peerLogger = Logger.getLogger("Peer_Logs");
            this.peerLogger.setUseParentHandlers(false);
            this.peerLogger.addHandler(this.peerLogFileHandler);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized void outgoingTCPConnectionLog(String peer_id) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO,
                    "[" + currTime + "]: Peer [" + this.peerId + "] makes a connection to Peer " + "[" + peer_id + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void incomingTCPConnectionLog(String peer_id) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO,
                    "[" + currTime + "]: Peer [" + this.peerId + "] is connected from Peer " + "[" + peer_id + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void changeOfPreferredNeighboursLog(List<Integer> neighbours) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO,
                    "[" + currTime + "]: Peer [" + this.peerId + "] has the preferred neighbours " + neighbours + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void changeOfOptimisticallyUnchokedNeighbourLog(String peer) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                    + "] has the optimistically unchoked neighbour [" + peer + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void unChokingNeighbourLog(String peer) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO,
                    "[" + currTime + "]: Peer [" + this.peerId + "] is unchoked by [" + peer + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void chokingNeighbourLog(String peer) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId + "] is choked by [" + peer + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void receivedHaveLog(String peer, int index) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                    + "] received the ‘have’ message from [" + peer + "] for the piece [" + index + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void receivedInterestedLog(String peer) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                    + "] received the ‘interested’ message from [" + peer + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void receiveNotInterested(String peer) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                    + "] received the ‘not interested’ message from [" + peer + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void downloadedPieceLog(String peer, int ind, int pieces) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO,
                    "[" + currTime + "]: Peer [" + this.peerId + "] has downloaded the piece [" + ind
                            + "] from [" + peer + "]. Now the number of pieces it has is [" + pieces
                            + "].");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void downloadingCompleteLog() {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.INFO,
                    "[" + currTime + "]: Peer [" + this.peerId + "] has downloaded the complete file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void genericErrorLog(Exception ex) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            this.peerLogger.log(Level.SEVERE,
                    "[" + currTime + "]: Exception: " + ex.getMessage() + " ,Cause:" + ex.getCause());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void genericErrorLog(String msg) {
        try {
            Calendar c = Calendar.getInstance();
            String currTime = this.dateFormat.format(c.getTime());
            if (this.peerLogger != null)
                this.peerLogger.log(Level.SEVERE,
                        "[" + currTime + "]: Error Message: " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLogger() {
        try {
            if (this.peerLogFileHandler != null) {
                this.peerLogFileHandler.close();
            }
        } catch (Exception e) {
            System.out.println("Failed to close peer logger");
            e.printStackTrace();
        }
    }

}
