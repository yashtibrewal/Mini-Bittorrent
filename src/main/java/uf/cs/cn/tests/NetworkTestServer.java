package uf.cs.cn.tests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkTestServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3000);
        Socket connection = serverSocket.accept();
        ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());
        byte[] b = new byte[5];
        int bytes_read = inputStream.read(b);
        inputStream.close();
        serverSocket.close();
    }

}
