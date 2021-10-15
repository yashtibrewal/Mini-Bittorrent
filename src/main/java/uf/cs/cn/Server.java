package uf.cs.cn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {

    private static final int serverPort = 8001;

    public static void main(String[] args) throws Exception {
        System.out.println("Server state: Running");
        int clientNum = 1;
        try(ServerSocket listener = new ServerSocket(serverPort)) {
            while (true) {
                new ClientRequestManager(listener.accept(), clientNum).start();
                System.out.println("Connection established with " + clientNum);
                clientNum++;
            }
        }
    }

    private static class ClientRequestManager<inputStream> extends Thread {
        private String message;
        private Socket connection;

        private int index;

        public ClientRequestManager(Socket accept, int clientNum) {
            this.connection = accept;
            this.index = clientNum;
        }

        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(32);
            HandShakeMessage handShakeMessage = new HandShakeMessage();
            try(ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream())) {
                outputStream.flush();
                try {
                    while (true) {
                        byte bits[] = new byte[32];
                        inputStream.read(bits);
                        System.out.println("Received the following");
                        System.out.println(new String(bits));
                        outputStream.writeObject("ACK");
                        outputStream.flush();
                    }
                } catch (Exception ex) {
                    System.err.println(index + " Client: Unknown contents");
                    ex.printStackTrace();
                }
            } catch (IOException ex) {
                System.err.println(index + " Client: Disconnected prematurely");
                ex.printStackTrace();
            }
        }
    }
}

