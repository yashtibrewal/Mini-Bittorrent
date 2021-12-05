package uf.cs.cn.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;

public class FileMerger {

    private static String getPieceName(int num) {
        return "piece_" + num;
    }

    public static void mergeFile(String input_file_path, String out_put_path) {
        FileInputStream fileInputStream = null;
        byte[] buffer = new byte[CommonConfigFileReader.piece_size];
        System.out.println("Trying to read out put path " + out_put_path);
        System.out.println("Input stream is " + input_file_path);
        try (FileOutputStream fileOutputStream = new FileOutputStream((out_put_path))
        ) {
            // Total number of piece files would be
            int num_of_pieces = (int) Math.ceil(1.0 * CommonConfigFileReader.file_size / CommonConfigFileReader.piece_size);
            int read_bytes;
            int total_bytes_written = 0;
            for (int i = 1; i <= num_of_pieces; i++) {
                // read the piece_<i> file and buffer it out in the output_file
                fileInputStream = new FileInputStream(Paths.get(input_file_path, getPieceName(i)).toString());
                read_bytes = fileInputStream.read(buffer);
                total_bytes_written += read_bytes;
                fileOutputStream.write(buffer, 0, read_bytes);
                fileOutputStream.flush();
                fileInputStream.close();
            }
            System.out.println("bytes written " + total_bytes_written);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
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
        int k = 0;
        for (PeerInfoConfigFileReader.PeerInfo peerInfo : PeerInfoConfigFileReader.getPeerInfoList()) {
            peer_ids[k] = peerInfo.getPeer_id();
            k += 1;
        }

        String running_dir = System.getProperty("user.dir");

        for (int i = 0; i < peer_ids.length - 1; i += 1) {
            String folderPath = Paths.get(running_dir, Integer.toString(peer_ids[i])).toString();
            File folder = new File(folderPath);
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null)
                for (File listOfFile : listOfFiles) {
                    String tempFileName = listOfFile.getName();
                    if (tempFileName.startsWith("piece_")) {
                        String tempFilePath = Paths.get(folderPath, tempFileName).toString();
                        deleteFile(tempFilePath);
                    }
                }
        }
    }

}
