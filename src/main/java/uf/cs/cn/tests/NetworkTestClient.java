package uf.cs.cn.tests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkTestClient {

    public static void main(String args[]) throws IOException {
        Socket socket = new Socket("localhost",3000);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        byte[] output = new byte[]{(byte)127,(byte)128,(byte)159,(byte)200,(byte)255};
        outputStream.write(output);
        outputStream.close();
        socket.close();
    }

}
