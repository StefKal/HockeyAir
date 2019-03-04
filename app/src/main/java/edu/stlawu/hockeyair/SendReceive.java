package edu.stlawu.hockeyair;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class SendReceive extends Thread {

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    String textSent = "";
    String coordinates = "";
    String puckCoordinates = "";
    String velocities = "";


    static final int MESSAGE_READ = 1;
    static final int COORDINATES = 2;


    SendReceive(Socket skt){
        socket = skt;

        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();


            printWriter =  new PrintWriter(outputStream, true);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    textSent = (String) msg.obj;
                    break;
                case 2:
                    coordinates = (String) msg.obj;
                    break;
                case 3:
                    puckCoordinates = (String) msg.obj;
                    break;
                case 4:
                    velocities = (String) msg.obj;
                    break;
            }
        }
    };





    @Override
    public void run() {

        int bytes;
        String buffer;

        while(socket != null){
            try {
                buffer = bufferedReader.readLine();
                Log.e("BUFFER", buffer);
                bytes = buffer.length();


                if (buffer.startsWith("t")  ||buffer.startsWith("g") ){

                    handler.sendMessage(handler.obtainMessage(1, bytes, -1, buffer));

                }
                if (buffer.startsWith("a")) {
                    handler.sendMessage(handler.obtainMessage(2, bytes, -1, buffer));

                }
                if (buffer.startsWith("b")){

                    handler.sendMessage(handler.obtainMessage(4, bytes, -1, buffer));
                }
                if (buffer.startsWith("c")){

                    handler.sendMessage(handler.obtainMessage(3, bytes, -1, buffer));

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void write(String toSend){
            printWriter.println(toSend);
    }



}