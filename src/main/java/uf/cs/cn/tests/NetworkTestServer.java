package uf.cs.cn.tests;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class NetworkTestServer {

    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket  = new ServerSocket(3000);
        Socket connection = serverSocket.accept();
        ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());
        byte[] b = new byte[5];
        int bytes_read = inputStream.read(b);
        System.out.println(bytes_read + " bytes read are "+ Arrays.toString(b));
        inputStream.close();
        serverSocket.close();
    }

}
