package uf.cs.cn.utils;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;


public class PeerLogging {

    private String logFileName;
    private String peerId;
    private FileHandler peerLogFileHandler;
    private SimpleDateFormat dateFormat = null;
    private Logger peerLogger;

    public PeerLogging(String peerId) {
        this.peerId = peerId;
        startLogger();
    }

    public void startLogger() {
        try {
            this.dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            this.logFileName = "log_peer_" + this.peerId + ".log";
            this.peerLogFileHandler = new FileHandler(Paths.get(System.getProperty("user.dir"),
                    String.valueOf(this.peerId),
                    this.logFileName).toString(),
                    false);
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
            this.peerLogFileHandler.setFormatter(new SimpleFormatter());
            this.peerLogger = Logger.getLogger("Peer_Logs");
            this.peerLogger.setUseParentHandlers(false);
            this.peerLogger.addHandler(this.peerLogFileHandler);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void outgoingTCPConnectionLog(String peer_id) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] makes a connection to Peer " + "[" + peer_id + "].");
    }

    public synchronized void incomingTCPConnectionLog(String peer_id) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] is connected from Peer " + "[" + peer_id + "].");
    }

    public synchronized void changeOfPreferredNeighboursLog(ArrayList<String> neighbours) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        String neighbourList = "";
        for (String neighbour : neighbours) {
            neighbourList += neighbour + ",";
        }
        neighbourList = neighbourList.substring(0, neighbourList.length() - 1);
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] has the preferred neighbours [" + neighbourList + "].");
    }

    public synchronized void changeOfOptimisticallyUnchokedNeighbourLog(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] has the optimistically unchoked neighbour [" + peer + "].");
    }

    public synchronized void unChokingNeighbourLog(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] is unchoked by [" + peer + "].");
    }

    public synchronized void chokingNeighbourLog(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId + "] is choked by [" + peer + "].");
    }

    public synchronized void receivedHaveLog(String peer, int index) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] received the ‘have’ message from [" + peer + "] for the piece [" + String.valueOf(index) + "].");
    }

    public synchronized void receivedInterestedLog(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] received the ‘interested’ message from [" + peer + "].");
    }

    public synchronized void receiveNotInterested(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] received the ‘not interested’ message from [" + peer + "].");
    }

    public synchronized void downloadedPieceLog(String peer, int ind, int pieces) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] has downloaded the piece [" + String.valueOf(ind)
                        + "] from [" + peer + "]. Now the number of pieces it has is [" + String.valueOf(pieces)
                        + "].");
    }

    public synchronized void downloadingCompleteLog() {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] has downloaded the complete file.");
    }

    public synchronized void genericErrorLog(Exception ex) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.SEVERE,
                "[" + currTime + "]: Exception: "+ ex.getMessage()+" ,Cause:"+ ex.getCause());
    }

    public void closeLogger() {
        try {
            if (this.peerLogFileHandler != null) {
                this.peerLogFileHandler.close();
            }
        }
        catch (Exception e) {
            System.out.println("Failed to close peer logger");
            e.printStackTrace();
        }
    }

}
