package uf.cs.cn.utils;

import java.io.*;
import java.nio.file.Paths;


/**
 * Read all Common info config file
 * example file content:
 * NumberOfPreferredNeighbors 3
 * UnchokingInterval 5
 * OptimisticUnchokingInterval 10
 * FileName tree.jpg
 * FileSize 24301474
 * PieceSize 16384
 */

public class CommonConfigFileReader {

    public static final String config_file_name = "Common.cfg";

    public static String file_name; // include full path
    static {
        try (
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(Paths.get(System.getProperty("user.dir"), config_file_name).toString()));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
            ){
            number_of_preferred_neighbours = Integer.parseInt(bufferedReader.readLine().split(" ")[1]);
            un_chocking_interval = Integer.parseInt(bufferedReader.readLine().split(" ")[1]);
            optimistic_un_choking_interval = Integer.parseInt(bufferedReader.readLine().split(" ")[1]);
            file_name = bufferedReader.readLine().split(" ")[1];
            file_size = Integer.parseInt(bufferedReader.readLine().split(" ")[1]);
            piece_size = Integer.parseInt(bufferedReader.readLine().split(" ")[1]);
            String[] fileNamesSplitted = file_name.split("\\.");
            file_extension = fileNamesSplitted[fileNamesSplitted.length-1];

        } catch (FileNotFoundException e) {
            System.out.println("Please check if the file exists");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int number_of_preferred_neighbours;
    public static int un_chocking_interval;
    public static int optimistic_un_choking_interval;
    public static int file_size;
    public static int piece_size;
    public static String file_extension;

}
