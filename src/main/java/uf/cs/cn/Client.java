package uf.cs.cn;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String args[]) {
        Client client = new Client();
        client.run();

//        HandShakeMessage handShakeMessage = new HandShakeMessage();
//        handShakeMessage.setPeer_id(new char[]{'B','A','K','C'});
//        System.out.println(handShakeMessage.getMessage());
    }

    void run() {
        byte bytes[] = new byte[32];
        try {
            Socket requestSocket = new Socket("localhost", 8001);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(requestSocket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Connected with server- remote port 8001");
            while (true) {
                System.out.print("Sending: ");
                System.out.println(bytes);
                HandShakeMessage handShakeMessage = new HandShakeMessage();
                handShakeMessage.setPeer_id(new char[]{'B','A','K','C'});
                bytes = handShakeMessage.getMessage().getBytes();
                objectOutputStream.write(bytes);
                objectOutputStream.flush();
                String receivedData = (String) objectInputStream.readObject();
                System.out.println("Received message from server: " + receivedData);
                Thread.sleep(5000);
            }
        } catch (Exception ex) {
            System.err.println(ex.getCause() + " -Error encountered when sending data to remote server.");
            ex.printStackTrace();
        }
    }

}
