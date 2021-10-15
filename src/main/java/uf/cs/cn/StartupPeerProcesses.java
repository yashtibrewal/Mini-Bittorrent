package uf.cs.cn;

public class StartupPeerProcesses {
    public static void main(String[] args) {
        String username = "punakshi.chaand";
        String locationAtServer = "/cise/homes/punakshi.chaand/punakshi/CN-Group29";
        String rsaKeyLocationInLocal = "/Users/punakshichaand/cngroup29";
        try {
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-00.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1001 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-01.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1002 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-02.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1003 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-03.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1004 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-04.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1005 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-05.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1006 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-06.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1007 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-08.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1008 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-07.cise.ufl.edu cd " + locationAtServer
                    + " ; java PeerProcess 1009 ");
        } catch (Exception e) {
            System.err.println("Unable to start the remote peer Processes as expected");
            e.printStackTrace();
        }
    }
}
