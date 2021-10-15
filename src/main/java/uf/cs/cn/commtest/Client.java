package uf.cs.cn.commtest;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String args[]) {
        Client client = new Client();
        client.run();
    }

    void run() {
        try (Socket requestSocket = new Socket("localhost", 8001);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(requestSocket.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected with server- remote port 8001");
            while (true) {
                System.out.print("Ready to read in data: ");
                String message = bufferedReader.readLine();
                objectOutputStream.flush();
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                String receivedData = (String) inputStream.readObject();
                System.out.println("Received message from server: " + receivedData);
            }
        } catch (ClassNotFoundException | IOException ex) {
            System.err.println(ex.getCause() + " -Error encountered when sending data to remote server.");
            ex.printStackTrace();
        }
    }

}
