package edu.stlawu.hockeyair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class Panel extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable {

    private String status;

    private MainThread mainThread;

    private boolean gameOver=false;

    private Board theBoard;
    private Paddle player;
    private Puck puck;

    private Point playerPoint;
    private Point puckPoint;
    private byte[] playerpointmessage;

    private Paddle opponent;
    private Point opponentPoint;

    float oldX;
    float oldY;

    double myPaddleOldX;
    double myPaddleOldY;


    int playerPaddleVelocityX;
    int playerPaddleVelocityY;

    int opponentPaddleVelocityX;
    int opponentPaddleVelocityY;

    int puckVelocityX;
    int puckVelocityY;
    int playerCollisionMultiplier=0;
    int opponentCollisionMultipler=0;


    int playerScore;
    int opponentScore;

    SurfaceHolder myHolder;
    Thread myThread = null;
    boolean isRunning = false;

    public Panel(Context context, String status){
        super(context);
        myHolder = getHolder();
        isRunning = true;
        myThread = new Thread(this);
        myThread.start();
        //mainThread.setRunning(true);

        //mainThread.start();
        this.status = status;
        theBoard = new Board(Color.MAGENTA);
        player = new Paddle(new RectF(ScreenConstants.SCREEN_WIDTH/2 ,ScreenConstants.SCREEN_HEIGHT,
                ScreenConstants.SCREEN_WIDTH/2  + 10,
                0),
                90,  Color.RED,Color.WHITE);

        opponent= new Paddle(new RectF(ScreenConstants.SCREEN_WIDTH/2 ,ScreenConstants.SCREEN_HEIGHT,
                ScreenConstants.SCREEN_WIDTH/2  + 10,
                0),
                90,  Color.RED,Color.WHITE);

        puck = new Puck(new RectF((ScreenConstants.SCREEN_WIDTH/2)- 80, ScreenConstants.SCREEN_HEIGHT/2 - 80,
                (ScreenConstants.SCREEN_WIDTH/2) + 80 , ScreenConstants.SCREEN_HEIGHT/2 + 80),
                Color.rgb(182,33,45), 50);

        playerScore = 0;
        opponentScore = 0;


        playerPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, 3*ScreenConstants.SCREEN_HEIGHT/4);
        opponentPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/4 );
        puckPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/2);

        player.update(playerPoint);
        opponent.update(opponentPoint);
        puck.update(puckPoint);

        puckVelocityX = 0;
        puckVelocityY = 0;
        playerPaddleVelocityX = 0;
        playerPaddleVelocityY = 0;
        opponentPaddleVelocityX = 0;
        opponentPaddleVelocityY = 0;

    }

    //gameLoop
    public void update(){
        if(!gameOver) {
            if(!JoinGameActivity.sendReceive.textSent.equals(status)){
               // opponentPoint = JoinGameActivity.sendReceive
            }
        }
    }

    public void getPaddleVelocity(){
        playerPaddleVelocityX = (int) ((player.getPaddle().centerX() - myPaddleOldX)*30);
        playerPaddleVelocityY = (int) ((player.getPaddle().centerY() - myPaddleOldY)*30);
    }

    public void goal(){
        playerPoint = new Point(ScreenConstants.SCREEN_WIDTH/2,3*ScreenConstants.SCREEN_HEIGHT/4);
        puckPoint = new Point(ScreenConstants.SCREEN_WIDTH/2,ScreenConstants.SCREEN_HEIGHT/2);

        oldX=ScreenConstants.SCREEN_WIDTH/2;
        oldY=3*ScreenConstants.SCREEN_HEIGHT/4;

        player.update(playerPoint);
        puck.update(puckPoint);

        puckVelocityX=0;
        puckVelocityY=0;
    }
//
//    //checks for intercepts with the goal
//    public void passTheGoal(){
//        if(theBoard.goalTouch(puck)){
//            if(theBoard.scoredGoal(puck)==Board.MYGOAL)opponentScore++;
//            else playerScore++;
//            gameOver=true;
//        }
//    }

    public void puckIntersects(){

        // if puck collides with player paddle
        if(puck.collides(player)){
            if(playerCollisionMultiplier == 0){
                playerCollisionMultiplier++;
                puckVelocityX = -puckVelocityX + playerPaddleVelocityX;
                puckVelocityY = -puckVelocityY + playerPaddleVelocityY;

            }
        }
        if(playerCollisionMultiplier > 0){
            playerCollisionMultiplier++;
        }
        if(playerCollisionMultiplier == 10){
            playerCollisionMultiplier = 0;
        }

        // if puck collides with opponent paddle
        if (puck.collides(opponent)){
            if(opponentCollisionMultipler == 0){
                opponentCollisionMultipler++;
                puckVelocityX = -puckVelocityX + opponentPaddleVelocityX;
                puckVelocityY = -puckVelocityY + opponentPaddleVelocityY;
            }
        }
        if(opponentCollisionMultipler > 0){
            opponentCollisionMultipler++;
        }
        if(opponentCollisionMultipler == 10){
            opponentCollisionMultipler = 0;
        }

    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        Paint paint = new Paint();
        canvas.drawColor(Color.WHITE);

        theBoard.draw(canvas);
        puck.draw(canvas);
        opponent.draw(canvas);
        player.draw(canvas);

        if(gameOver){

            paint.setTextSize(100);
            paint.setColor(Color.GREEN);
            drawScore(canvas, paint, playerScore + " - " + opponentScore);
        }
    }

    public void drawScore(Canvas canvas, Paint paint, String score){
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(80);
        Rect rect = new Rect();
        canvas.getClipBounds(rect);


        paint.getTextBounds(score, 0, score.length(), rect);
        canvas.drawText(score, 200, 200, paint);
        canvas.drawText(score, ScreenConstants.SCREEN_WIDTH - 280, ScreenConstants.SCREEN_HEIGHT - 200, paint);
    }

    // drag and drop

    private void updatePoint(float dx, float dy){
        oldX = playerPoint.x;
        oldY = playerPoint.y;
        playerPoint.set((int)(oldX+dx*8),(int)( oldY+dy*8));
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //mainThread = new MainThread(getHolder(),this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        while(true){
//            try{
//                mainThread.setRunning(false);
//                mainThread.join();
//
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }

    }

    @Override
    public void run() {
        while(isRunning){
            if(!myHolder.getSurface().isValid())
                continue;

            Canvas canvas = myHolder.lockCanvas();
          //  update();
            draw(canvas);
            myHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN :
            break;
            case MotionEvent.ACTION_MOVE:

                float newX = event.getRawX();
                float newY = event.getRawY();

                if(player.getPaddle().contains(newX, newY)) {
                    if(newY < ScreenConstants.SCREEN_HEIGHT/2)
                        newY = ScreenConstants.SCREEN_HEIGHT/2;
                    // sendCoordinates.start();
                    playerPoint.set((int) newX, (int) newY);
                    player.update(playerPoint);
                    oldX = newX;
                    oldY = newY;
                }
                break;
        }
        return true;
    }
    Thread sendCoordinates = new Thread(new Runnable() {
        @Override
        public void run() {
            // Convert Point to Bytes so we can send it over
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(playerPoint);
            } catch (IOException e) {
                e.printStackTrace();
            }

            playerpointmessage = outputStream.toByteArray();
            JoinGameActivity.sendReceive.write(playerpointmessage);

            //specify the sender
            JoinGameActivity.sendReceive.write(status.getBytes());
        }
    });

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}