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
    String coordinates = "";
    String puckCoordinates = "";


    static final int MESSAGE_READ = 1;
    static final int COORDINATES = 2;


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
                case 1:
                    byte[] readbuff = (byte[]) msg.obj;
                    textSent = new String(readbuff, 0, msg.arg1);
                    break;


            }
        }
    };


    Handler paddleHandler = new Handler(Looper.getMainLooper()){
      @Override
      public void handleMessage(Message msg){
          switch (msg.what){
              case 2:
                  byte[] read = (byte[]) msg.obj;
                  coordinates = new String(read, 0, msg.arg1);
                  break;
          }
      }
    };

    Handler puckHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 3:
                    byte[] puckRead = (byte[]) msg.obj;
                    puckCoordinates = new String(puckRead, 0, msg.arg1);
                    break;
            }
        }
    };



    @Override
    public void run() {
        byte[] buffer = new byte[500];
        int bytes;

        while(socket != null){
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        if (bytes < 5){

                            Message msg = handler.obtainMessage(1, bytes, -1, buffer);
                            handler.sendMessage(msg);
                        }else if (bytes > 5 && bytes < 10) {
                            Message msg2 = puckHandler.obtainMessage(3, bytes, -1, buffer);
                            puckHandler.sendMessage(msg2);
                        }else{

                            Message msg = paddleHandler.obtainMessage(2, bytes, -1, buffer);
                            paddleHandler.sendMessage(msg);

                        }

                        
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
    }


    public void write(byte[] bytes){
        try {

            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}