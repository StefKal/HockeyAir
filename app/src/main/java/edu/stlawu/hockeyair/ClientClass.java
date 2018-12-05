package edu.stlawu.hockeyair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientClass extends Thread {
    public Socket socket;
    private String hostAdd;

    ClientClass(InetAddress hostAddress){
        hostAdd = hostAddress.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
            JoinGameActivity.sendReceive = new SendReceive(socket);
            JoinGameActivity.sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
