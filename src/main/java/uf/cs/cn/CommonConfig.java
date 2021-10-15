package uf.cs.cn;

import java.io.FileReader;
import java.util.Scanner;

public class CommonConfig {
    public  int numberOfPreferredNeighbors;
    public  int unchokingInterval;
    public  int optimisticUnchokingInterval;
    public  String fileName;
    public  int fileSize;
    public  int portionSize;
    public void loadCommonFile() {
        try (FileReader fileReader = new FileReader("resources/Common.cfg");
             Scanner fReader = new Scanner(fileReader);) {
            while (fReader.hasNextLine()) {
                String line = fReader.nextLine();
                String[] dataPts = line.split(" ");
                if (dataPts[0].equals("NumberOfPreferredNeighbors")) {
                    numberOfPreferredNeighbors = Integer.parseInt(dataPts[1]);
                } else if (dataPts[0].equals("UnchokingInterval")) {
                    unchokingInterval = Integer.parseInt(dataPts[1]);
                } else if (dataPts[0].equals("OptimisticUnchokingInterval")) {
                    optimisticUnchokingInterval = Integer.parseInt(dataPts[1]);
                } else if (dataPts[0].equals("FileName")) {
                    fileName = dataPts[1];
                } else if (dataPts[0].equals("FileSize")) {
                    fileSize = Integer.parseInt(dataPts[1]);
                } else if (dataPts[0].equals("PieceSize")) {
                    portionSize = Integer.parseInt(dataPts[1]);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
