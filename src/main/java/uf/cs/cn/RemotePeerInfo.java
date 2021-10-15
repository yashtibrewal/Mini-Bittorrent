package uf.cs.cn;

public class RemotePeerInfo {
    public String peerId;
    public String peerAddress;
    public int peerPort;
    public boolean containsFile;

    public RemotePeerInfo(String pId, String pAddress, String pPort, String hasFile) {
        this.peerId = pId;
        this.peerAddress = pAddress;
        this.peerPort = Integer.parseInt(pPort);
        this.containsFile = Boolean.getBoolean(hasFile);
    }
}
