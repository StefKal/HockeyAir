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

    // These need to be visible to Panel for communication
    String textSent = "";
    String paddleCoordinates = "";
    String puckCoordinates = "";
    String velocities = "";

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private static final int MESSAGE_READ = 1;
    private static final int PADDLE_COORDINATES = 2;
    private static final int PADDLE_VELOCITIES = 3;
    private static final int PUCK_COORDINATES = 4;


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


    // sets instance variables according to msg.what
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    textSent = (String) msg.obj;
                    break;
                case PADDLE_COORDINATES:
                    paddleCoordinates = (String) msg.obj;
                    break;
                case PUCK_COORDINATES:
                    puckCoordinates = (String) msg.obj;
                    break;
                case PADDLE_VELOCITIES:
                    velocities = (String) msg.obj;
                    break;
            }
        }
    };

    // constructs messages in queue and sends them to the handler
    @Override
    public void run() {
        String buffer;

        while(socket != null){
            try {
                buffer = bufferedReader.readLine();


                if (buffer.startsWith("t")  ||buffer.startsWith("g") ){ //  one sends "true" and other sends "got"
                    handler.sendMessage(handler.obtainMessage(MESSAGE_READ, -1, -1, buffer));
                }
                if (buffer.startsWith("a")) {
                    handler.sendMessage(handler.obtainMessage(PADDLE_COORDINATES, -1, -1, buffer));
                }
                if (buffer.startsWith("b")){
                    handler.sendMessage(handler.obtainMessage(PADDLE_VELOCITIES, -1, -1, buffer));
                }
                if (buffer.startsWith("c")){
                    handler.sendMessage(handler.obtainMessage(PUCK_COORDINATES, -1, -1, buffer));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // writs messages to printWriter
    public void write(String toSend){
            printWriter.println(toSend);
    }
}