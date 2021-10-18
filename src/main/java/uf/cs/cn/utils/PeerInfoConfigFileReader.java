package uf.cs.cn.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Read all the peer information config file
 * example file content:
 * 1001 lin114-00.cise.ufl.edu 6001 1
 * 1002 lin114-01.cise.ufl.edu 6001 0
 * 1003 lin114-02.cise.ufl.edu 6001 0
 */
public class PeerInfoConfigFileReader {
    public static final String config_file_name = "PeerInfo.cfg";
    private static ArrayList<PeerInfo> peer_info_lists = new ArrayList<>();
    static {
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(Paths.get(System.getProperty("user.dir"), config_file_name).toString()));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ){
            String[] words; String line;
            while(true) {
                line = bufferedReader.readLine();
                if(line==null) {
                    break;
                }
                words = line.split(" ");
                peer_info_lists.add(new PeerInfo(Integer.parseInt(words[0]),words[1],Integer.parseInt(words[2]),words[3].equals("1")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PeerInfo> getPeerInfoList() {
        return peer_info_lists;
    }

    public static class PeerInfo {
        private int peer_id;
        private String peer_host_name;
        private int listening_port;
        private boolean has_file;

        // TODO: create setters and call them inside the constructor, purpose is sanity check for values
        PeerInfo(int peer_id, String peer_host_name, int listening_port, boolean has_file) {
            this.peer_id = peer_id;
            this.peer_host_name = peer_host_name;
            this.listening_port = listening_port;
            this.has_file = has_file;
        }

        public int getPeer_id(){
            return peer_id;
        }

        public String getPeer_host_name() {
            return peer_host_name;
        }

        public int getListening_port() {
            return listening_port;
        }

        public boolean isHas_file() {
            return has_file;
        }

        public String toString() {
            return this.getPeer_id() + " " + this.getPeer_host_name() + " " + this.getListening_port() + " " + (this.isHas_file() ? 1 : 0);
        }
    }
//    Just for testing
    public static void main(String[] args) {
        for(PeerInfo peerInfo:PeerInfoConfigFileReader.peer_info_lists){
            System.out.println(peerInfo);
        }
    }
}
