package uf.cs.cn.peer;

import uf.cs.cn.utils.BitFieldUtils;

import java.util.ArrayList;

class PeerConfig {
    ArrayList<Boolean> file_chunks;
    int peer_id;
    public int download_bandwidth_data_counter;
    boolean is_interested;

    PeerConfig(int peer_id) {
        this.peer_id = peer_id;
        file_chunks = new ArrayList<>();
        for (int i = 0; i < BitFieldUtils.getNumberOfChunks(); i++) {
            file_chunks.add(false);
        }
        download_bandwidth_data_counter++;
    }

    /**
     * Checks if the present peer has received all the chunks.
     * @return
     */
    public boolean gotAllChunks() {
        for (Boolean file_chunk : file_chunks) {
            if (!file_chunk) return false;
        }
        return true;
    }

    public void setDownload_bandwidth_data_counter(int download_bandwidth_data_counter) {
        this.download_bandwidth_data_counter = download_bandwidth_data_counter;
    }

    void resetCounter() {
        this.download_bandwidth_data_counter = 0;
    }

}
