package uf.cs.cn.utils;

import java.io.*;
import java.nio.file.Paths;

/**
 * Aim is to split a file into pieces
 */
public class FileSplitter {
    public static void splitFile(String input_file_path, String out_put_path) {
        try(FileInputStream fileInputStream = new FileInputStream(input_file_path)) {
            byte[] piece = new byte[CommonConfigFileReader.piece_size];
            int counter = 1;
            String output_file_name;

            int total_bytes_read = 0;
            while (true) {
                output_file_name = "piece_" + counter;
                int number_of_characters_read = fileInputStream.read(piece);
                total_bytes_read += number_of_characters_read;
                if(number_of_characters_read == -1) {
                    break;
                }
                try(FileOutputStream fileOutputStream = new FileOutputStream(Paths.get(out_put_path, output_file_name).toString());) {
                    fileOutputStream.write(piece, 0, number_of_characters_read);
                }
                counter++;
            }
            System.out.println(total_bytes_read);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
        String peer_id = String.valueOf(PeerInfoConfigFileReader.getPeerInfoList().get(0).getPeer_id());
        FileSplitter.splitFile(
                Paths.get(running_dir, peer_id, CommonConfigFileReader.file_name).toString(),
                Paths.get(running_dir, peer_id).toString());
        Thread.sleep(500000);
    }
}
