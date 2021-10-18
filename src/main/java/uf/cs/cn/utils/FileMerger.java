package uf.cs.cn.utils;

import uf.cs.cn.peer.Peer;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.util.ArrayList;

public class FileMerger {

    private static String getPieceName(int num) {
        return "piece_" + num;
    }

    public static void mergeFile(String input_file_path, String out_put_path) {
        FileInputStream fileInputStream = null;
        byte[] buffer = new byte[CommonConfigFileReader.piece_size];
        try(FileOutputStream fileOutputStream = new FileOutputStream(new File(out_put_path));
        ) {

            // Total number of piece files would be
            int num_of_pieces = (int)Math.ceil(1.0*CommonConfigFileReader.file_size / CommonConfigFileReader.piece_size);
            int read_bytes;
            int total_bytes_written = 0;
            for(int i =1;i  <= num_of_pieces;i++) {
                // read the piece_<i> file and buffer it out in the output_file
                fileInputStream = new FileInputStream(Paths.get(input_file_path,getPieceName(i)).toString());
                read_bytes = fileInputStream.read(buffer);
                total_bytes_written += read_bytes;
                fileOutputStream.write(buffer,0,read_bytes);
                fileOutputStream.flush();
            }
            System.out.println("bytes written "+total_bytes_written);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileInputStream!=null)
                    fileInputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void deleteFile(String fileName) {
        try {
            Files.delete(Path.of(fileName));
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", Path.of(fileName));
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", Path.of(fileName));
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }
    public static void deleteChunks() {
        int[] peer_ids = new int[PeerInfoConfigFileReader.getPeerInfoList().size()];
        int k=0;
        for(PeerInfoConfigFileReader.PeerInfo peerInfo:PeerInfoConfigFileReader.getPeerInfoList()) {
            peer_ids[k] = peerInfo.getPeer_id();
            k += 1;
        }

        String running_dir = System.getProperty("user.dir");
        String tempFilePath = Paths.get(running_dir, "1001", "piece_1").toString();
        deleteFile(tempFilePath);

        String tempFilePath2 = Paths.get(running_dir, "1001", "piece_2").toString();
        deleteFile(tempFilePath2);


//        for (int i =0; i < peer_ids.length-1; i += 1) {
//            String folderPath = Paths.get(running_dir, Integer.toString(peer_ids[i])).toString();
//            File folder = new File(folderPath);
//            File[] listOfFiles = folder.listFiles();
//            for (int j = 0; j < listOfFiles.length; j++) {
//                String tempFileName = listOfFiles[j].getName();
//                if (tempFileName.startsWith("piece_")) {
//                    System.out.println("Checking for file " + tempFileName);
//                    String tempFilePath = Paths.get(folderPath, tempFileName).toString();
//                    //File tempFile = new File(tempFilePath);
//                    deleteFile(tempFilePath);
//            }
//            }
//        }
    }

    public static void main(String[] args) {
        String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
        String peer_id = String.valueOf(PeerInfoConfigFileReader.getPeerInfoList().get(0).getPeer_id());
        FileMerger.mergeFile(
                Paths.get(running_dir, peer_id).toString(),
                Paths.get(running_dir, peer_id, "merged_file."+CommonConfigFileReader.file_extension).toString());

        //deleteChunks();
//        String tempFilePath = Paths.get(running_dir, "1001", "piece_1").toString();
//        deleteFile(tempFilePath);
//
//        String tempFilePath2 = Paths.get(running_dir, "1001", "piece_2").toString();
//        deleteFile(tempFilePath2);
    }
}
