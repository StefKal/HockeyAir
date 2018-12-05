package edu.stlawu.hockeyair;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendReceive extends Thread {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    String textSent = "";

    static final int MESSAGE_READ = 1;


    SendReceive(Socket skt){
         socket = skt;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readbuff = (byte[]) msg.obj;
                    textSent = new String(readbuff, 0, msg.arg1);
                    break;
            }
        }
    };

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;


        while(socket != null){
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0){
                    handler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
