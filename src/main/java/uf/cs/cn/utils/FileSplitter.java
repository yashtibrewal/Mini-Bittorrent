package uf.cs.cn.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Aim is to split a file into pieces
 */
public class FileSplitter {
    public static void splitFile(String input_file_path, String out_put_path) {
        try (FileInputStream fileInputStream = new FileInputStream(input_file_path)) {
            byte[] piece = new byte[CommonConfigFileReader.piece_size];
            byte[] output;
            int counter = 1;
            String output_file_name;

            int total_bytes_read = 0;
            while (true) {
                output_file_name = "piece_" + counter;
                int number_of_characters_read = fileInputStream.read(piece);
                System.out.println("number_of_characters_read " + number_of_characters_read);
                total_bytes_read += number_of_characters_read;
                if (number_of_characters_read == -1) {
                    break;
                }
                output = new byte[number_of_characters_read];
                for (int i = 0; i < number_of_characters_read; i++) {
                    output[i] = piece[i];
                }
                try (FileOutputStream fileOutputStream = new FileOutputStream(Paths.get(out_put_path, output_file_name).toString())) {
                    fileOutputStream.write(output);
                    fileOutputStream.flush();
                }
                counter++;
            }
            System.out.println("----FILE SPLITTER " + total_bytes_read);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
