package uf.cs.cn.exec;

public class StartupPeerProcesses {
    public static void main(String[] args) {
        String username = "punakshi.chaand";
        String locationAtServer = "/cise/homes/punakshi.chaand/punakshi/CN-Group29";
        String rsaKeyLocationInLocal = "/Users/punakshi.chaand/punakshi/cngroup29";
        try {
            Runtime.getRuntime().exec("javac Main.java");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-00.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1001 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-01.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1002 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-02.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1003 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-03.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1004 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-04.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1005 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-05.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1006 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-06.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1007 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-07.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1008 ");
            Runtime.getRuntime().exec("ssh -i " + rsaKeyLocationInLocal + " " + username +
                    "@lin114-08.cise.ufl.edu cd " + locationAtServer
                    + " ; java Main 1009 ");
        } catch (Exception e) {
            System.err.println("Unable to start remote Peer Processes as expected");
            e.printStackTrace();
        }
    }
}