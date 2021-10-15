package uf.cs.cn;

public class SinglePeerProcess {
    public static void printCommonConfig(CommonConfig cfg){
        System.out.println(cfg.numberOfPreferredNeighbors);
        System.out.println(cfg.unchokingInterval);
        System.out.println(cfg.optimisticUnchokingInterval);
        System.out.println(cfg.fileName);
        System.out.println(cfg.fileSize);
        System.out.println(cfg.portionSize);
    }
    public static void printPeerConfig(PeerInfoConfig peerInfoConfig){
        for (RemotePeerInfo i: peerInfoConfig.peerInfoList){
            System.out.println(i.toString());
        }
    }
    public static void main(String[] args) {
        printCommonConfig(new CommonConfig());
        printPeerConfig(new PeerInfoConfig());
        new PeerAdmin(args[0]);
    }
}
