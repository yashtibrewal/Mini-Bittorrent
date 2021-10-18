package uf.cs.cn.utils;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileMerger {


    public static void mergeFile(String input_file_path, String out_put_path) {
        File folder = new File(input_file_path);
        File[] listOfFiles = folder.listFiles();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ArrayList<File> PiecesOfFiles = new ArrayList<File>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().startsWith("piece_")) {
                    PiecesOfFiles.add(file);
                }
            }
        }

        int numberOfChunks = PiecesOfFiles.size();
        byte[] buffer = new byte[CommonConfigFileReader.piece_size];
        int len = 0;
        try { fos = new FileOutputStream(out_put_path);
            for (int i = 0; i < numberOfChunks; i++) {
                fis = new FileInputStream(listOfFiles[i]);
                len = fis.read(buffer);
                fos.write(buffer, 0, len);
            }
            System.out.println("Merge catalog file:" + input_file_path + "Complete, the generated file is:" + out_put_path);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }


    }
    public static void main(String[] args) {
        String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
        String peer_id = String.valueOf(PeerInfoConfigFileReader.getPeerInfoList().get(0).getPeer_id());
        FileSplitter.splitFile(
                Paths.get(running_dir, peer_id,"/").toString(),
                Paths.get(running_dir, peer_id, "merged_file.png").toString());
    }
}
