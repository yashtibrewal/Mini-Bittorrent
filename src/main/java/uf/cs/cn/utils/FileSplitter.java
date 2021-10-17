package uf.cs.cn.utils;

import java.io.*;
import java.nio.file.Paths;

// TODO: Debug why is it creating less number of files than expected, only 22884321 bytes used out of total 24301474, any hidden concept?
/**
 * Aim is to split a file into pieces
 */
public class FileSplitter {
    public static void splitFile(String input_file_path, String out_put_path) {
        try (
            BufferedReader bufferedReader = new BufferedReader(new FileReader(input_file_path))){
            char[] piece = new char[CommonConfigFileReader.piece_size];
            int counter = 1;
            String output_file_name;
            FileWriter fileWriter;
            int total_bytes_read = 0;
            while (true) {
                output_file_name = "piece_" + counter;
                int number_of_characters_read = bufferedReader.read(piece);
                total_bytes_read += number_of_characters_read;
                if(number_of_characters_read == -1) {
                    break;
                }
                fileWriter = new FileWriter(Paths.get(out_put_path, output_file_name).toString());
                fileWriter.write(piece);
                fileWriter.close();
                counter++;
            }
            System.out.println(total_bytes_read);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String running_dir = System.getProperty("user.dir"); // gets the base directory of the project
        String peer_id = String.valueOf(PeerInfoConfigFileReader.peerInfoLists.get(0).getPeer_id());
        FileSplitter.splitFile(
                Paths.get(running_dir, peer_id, CommonConfigFileReader.file_name).toString(),
                Paths.get(running_dir, peer_id).toString());
    }
}
