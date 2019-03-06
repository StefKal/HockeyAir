package edu.stlawu.hockeyair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;


import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@SuppressLint("ViewConstructor")
public class Panel extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable {

    private String status;


    private Board theBoard;
    private Paddle player;
    private Puck puck;

    private Point playerPoint;
    private Point puckPoint;


    private Paddle opponent;
    private Point opponentPoint;

    float oldX;
    float oldY;

    private VelocityTracker mVelocityTracker = null;


    float playerPaddleVelocityX;
    float playerPaddleVelocityY;

    float opponentPaddleVelocityX;
    float opponentPaddleVelocityY;

    float puckVelocityX;
    float puckVelocityY;


    int playerScore;
    int opponentScore;

    SurfaceHolder myHolder;
    Thread myThread;
    boolean isRunning;

    public Panel(Context context, String status){
        super(context);

        this.isRunning = true;
        this.myThread = new Thread(this);
        this.myThread.start();
        this.status = status;

        initialize();
        create_and_update_points();

    }

    private void initialize(){


        this.playerScore = 0;
        this.opponentScore = 0;
        this.puckVelocityX = 0;
        this.puckVelocityY = 0;
        this.playerPaddleVelocityX = 0;
        this.playerPaddleVelocityY = 0;
        this.opponentPaddleVelocityX = 0;
        this.opponentPaddleVelocityY = 0;
        this.sendCoordinates.start();

        this.theBoard = new Board(Color.MAGENTA);
        this.player = new Paddle(new RectF(ScreenConstants.SCREEN_WIDTH/2 ,ScreenConstants.SCREEN_HEIGHT,
                ScreenConstants.SCREEN_WIDTH/2  + 10,
                0),
                90,  Color.RED,Color.WHITE);

        this.opponent= new Paddle(new RectF(ScreenConstants.SCREEN_WIDTH/2 ,ScreenConstants.SCREEN_HEIGHT,
                ScreenConstants.SCREEN_WIDTH/2  + 10,
                0),
                90,  Color.RED,Color.WHITE);

        this.puck = new Puck(new RectF((ScreenConstants.SCREEN_WIDTH/2)- 80, ScreenConstants.SCREEN_HEIGHT/2 - 80,
                (ScreenConstants.SCREEN_WIDTH/2) + 80 , ScreenConstants.SCREEN_HEIGHT/2 + 80),
                Color.rgb(182,33,45), 50);
    }


    private void create_and_update_points(){

        this.playerPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, 3*ScreenConstants.SCREEN_HEIGHT/4);
        this.opponentPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/4 );
        this.puckPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/2);

        this.player.update(playerPoint);
        this.opponent.update(opponentPoint);
        this.puck.update(puckPoint);


    }
    //gameLoop
    public void update(){
        boolean gameOver = false;

        if(!gameOver) {

            String opponentCoord = JoinGameActivity.sendReceive.paddleCoordinates; // coords come in like this "a,500,500"
            String[] opponentCoordList = opponentCoord.split(","); // ["a","500","500"]
            if (opponentCoordList.length > 1){ // need to make sure our list is not empty
                int opponentX = Integer.parseInt(opponentCoordList[1]);
                int opponentY = Integer.parseInt(opponentCoordList[2]);

                // handles reflection of paddle to the opposite tablet properly
                int difY = opponentY - ScreenConstants.SCREEN_HEIGHT/2;
                opponentY = ScreenConstants.SCREEN_HEIGHT/2 - difY;
                int difX = opponentX - ScreenConstants.SCREEN_WIDTH/2;
                opponentX = ScreenConstants.SCREEN_WIDTH/2 -difX;

                //updates
                opponentPoint.set(opponentX, opponentY);
                opponent.update(opponentPoint);
            }


            String opponentVelocities = JoinGameActivity.sendReceive.velocities;
            String[] opponentVelocitesList = opponentVelocities.split(",");

            if (opponentVelocitesList.length > 1){
                int opponentVelocityX = Integer.parseInt(opponentVelocitesList[1]);
                int opponentVelocityY = Integer.parseInt(opponentVelocitesList[2]);

                opponentPaddleVelocityX =  -opponentVelocityX;
                opponentPaddleVelocityY =  -opponentVelocityY;
            }


            // the client reads the puck coordinates
            if (status.equals("client")){
                String puckCoord = JoinGameActivity.sendReceive.puckCoordinates;  // coords come in like this "c,500,500"
                String[] puckCoordList = puckCoord.split(","); // ["c","500","500"]
                if (puckCoordList.length > 1){ // need to make sure our list is not empty
                    int puckX = Integer.parseInt(puckCoordList[1]);
                    int puckY = Integer.parseInt(puckCoordList[2]);

                    // handles reflection of puck to the opposite tablet properly
                    int puckDifY = puckY - ScreenConstants.SCREEN_HEIGHT/2;
                    puckY = ScreenConstants.SCREEN_HEIGHT/2 - puckDifY;
                    int puckDifX = puckX - ScreenConstants.SCREEN_WIDTH/2;
                    puckX = ScreenConstants.SCREEN_WIDTH/2 -puckDifX;

                    //updates
                    puckPoint.set(puckX, puckY);
                    puck.update(puckPoint);
                }
            }
        }
    }


    //checks if the ball intersected any of the paddles or walls
    public void ballIntersectUpdate(){

        puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
        puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
        puckPoint.set(puckPoint.x, puckPoint.y);

        float playerdx = puckPoint.x - playerPoint.x;
        float playerdy = puckPoint.y - playerPoint.y;

        float opponentdx = puckPoint.x - opponentPoint.x;
        float opponentdy = puckPoint.y - opponentPoint.y;

        float playerdistance = (float) Math.hypot(playerdx, playerdy);
        float opponentdistance = (float) Math.hypot(opponentdx, opponentdy);

        // PLAYER COLLISION WITH PUCK
        if (playerdistance < puck.getPuckSize() + player.getSize()) {
            puckVelocityX = playerPaddleVelocityX;
            puckVelocityY = playerPaddleVelocityY;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
            puckPoint.set(puckPoint.x, puckPoint.y);
        }

        // OPPONENT COLLISION WITH PUCK
        if (opponentdistance < puck.getPuckSize() + opponent.getSize()){
            puckVelocityX = opponentPaddleVelocityX;
            puckVelocityY = opponentPaddleVelocityY;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
            puckPoint.set(puckPoint.x, puckPoint.y);
        }


        // 4-Wall collisions
        if (puckPoint.x + puck.getPuckSize() > ScreenConstants.SCREEN_WIDTH){
            puckVelocityX = -puckVelocityX;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.set(puckPoint.x, puckPoint.y);
        }else if(puckPoint.x - puck.getPuckSize() < 0){
            puckVelocityX = -puckVelocityX;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.set(puckPoint.x, puckPoint.y);
        }else if(puckPoint.y - puck.getPuckSize() < 0){
            puckVelocityY = -puckVelocityY;
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
            puckPoint.set(puckPoint.x, puckPoint.y);
        }else if(puckPoint.y + puck.getPuckSize() > ScreenConstants.SCREEN_HEIGHT){
            puckVelocityY = -puckVelocityY;
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
            puckPoint.set(puckPoint.x, puckPoint.y);
        }

        puck.update(puckPoint);
        puckVelocityY = (float) (puckVelocityY * 0.99);// Friction
        puckVelocityX = (float) (puckVelocityX * 0.99);// Friction
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


    // Draws everything on board
    public void draw(Canvas canvas){
        super.draw(canvas);
        if (canvas!= null) {
            canvas.drawColor(Color.WHITE);

            theBoard.draw(canvas);
            puck.draw(canvas);
            opponent.draw(canvas);
            player.draw(canvas);
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


    // THIS IS THE MAIN LOOP
    @Override
    public void run() {
        while(isRunning){
            myHolder = getHolder();

            if(!myHolder.getSurface().isValid())
                continue;

            Canvas canvas = myHolder.lockCanvas();
            update();
            ballIntersectUpdate();
            if (canvas!= null) {
                draw(canvas);
                myHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN :
                if(mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }else{
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                float newX = event.getRawX();
                float newY = event.getRawY();

                if(player.getPaddle().contains(newX, newY)) {
                    if(newY < ScreenConstants.SCREEN_HEIGHT/2)
                        newY = ScreenConstants.SCREEN_HEIGHT/2;
                    mVelocityTracker.computeCurrentVelocity(10);
                    playerPaddleVelocityX = mVelocityTracker.getXVelocity();
                    playerPaddleVelocityY = mVelocityTracker.getYVelocity();
                    playerPoint.set((int) newX, (int) newY);

                    player.update(playerPoint);
                    oldX = newX;
                    oldY = newY;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
        }


        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {


    }


    // THREAD FOR SENDING PADDLE & PUCK COORDINATES AS WELL AS PADDLE VELOCITIES
    final ScheduledExecutorService task = Executors.newScheduledThreadPool(1);

    Thread sendCoordinates = new Thread(new Runnable() {

        @Override
        public void run() {
        task.scheduleAtFixedRate(new Runnable() {
            public void run() {
                String playerX = String.valueOf(playerPoint.x);
                String playerY = String.valueOf(playerPoint.y);
                String coord = "a" + "," + playerX + "," + playerY;
                JoinGameActivity.sendReceive.write(coord);

                String playerVelocityX = String.valueOf((int)playerPaddleVelocityX);
                String playerVelocityY = String.valueOf((int)playerPaddleVelocityY);
                String velocities = "b" + "," + playerVelocityX + "," + playerVelocityY;
                JoinGameActivity.sendReceive.write(velocities);

                // the host ONLY handles puck coordinates
                if (status.equals("host")){
                    String puckX = String.valueOf(puckPoint.x);
                    String puckY = String.valueOf(puckPoint.y);
                    String puckCoord = "c" + "," + puckX + "," + puckY;
                    JoinGameActivity.sendReceive.write(puckCoord);
                }
            }
        }, 0, 4, TimeUnit.MILLISECONDS);
        }
    });

}