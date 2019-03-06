package edu.stlawu.hockeyair;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Panel extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable {

    private String status;


    private boolean gameOver=false;

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
    Thread myThread = null;
    boolean isRunning = false;

    public Panel(Context context, String status){
        super(context);


        isRunning = true;
        myThread = new Thread(this);
        myThread.start();

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
        sendCoordinates.start();


    }


    //gameLoop
    public void update(){
        if(!gameOver) {

            String opponentCoord = JoinGameActivity.sendReceive.paddleCoordinates;
            String[] opponentCoordList = opponentCoord.split(",");
            if (opponentCoordList.length > 1){
                int opponentX = Integer.parseInt(opponentCoordList[1]); // raw coords of player
                int opponentY = Integer.parseInt(opponentCoordList[2]);

                int difY = opponentY - ScreenConstants.SCREEN_HEIGHT/2;
                opponentY = ScreenConstants.SCREEN_HEIGHT/2 - difY;

                int difX = opponentX - ScreenConstants.SCREEN_WIDTH/2;
                opponentX = ScreenConstants.SCREEN_WIDTH/2 -difX;


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



            if (status.equals("client")){
                String puckCoord = JoinGameActivity.sendReceive.puckCoordinates;
                String[] puckCoordList = puckCoord.split(",");
                if (puckCoordList.length > 1){
                    int puckX = Integer.parseInt(puckCoordList[1]); // raw coords of player
                    int puckY = Integer.parseInt(puckCoordList[2]);
                    int puckDifY = puckY - ScreenConstants.SCREEN_HEIGHT/2;
                    puckY = ScreenConstants.SCREEN_HEIGHT/2 - puckDifY;

                    int puckDifX = puckX - ScreenConstants.SCREEN_WIDTH/2;
                    puckX = ScreenConstants.SCREEN_WIDTH/2 -puckDifX;
                    puckPoint.set(puckX, puckY);
                    puck.update(puckPoint);
                }
            }





        }

    }


    //checks if the ball intersected any of the mallets
    public void ballIntersectUpdate(){

        puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
        puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
        puckPoint.set(puckPoint.x, puckPoint.y);
        //puck.update(puckPoint);

        float playerdx = puckPoint.x - playerPoint.x;
        float playerdy = puckPoint.y - playerPoint.y;

        float opponentdx = puckPoint.x - opponentPoint.x;
        float opponentdy = puckPoint.y - opponentPoint.y;

        float playerdistance = (float) Math.hypot(playerdx, playerdy);
        float opponentdistance = (float) Math.hypot(opponentdx, opponentdy);
        
        if (playerdistance < puck.getPuckSize() + player.getSize()) {
            //They collide
            puckVelocityX = playerPaddleVelocityX;
            puckVelocityY = playerPaddleVelocityY;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));

            puckPoint.set(puckPoint.x, puckPoint.y);
            //puck.update(puckPoint);
        }
        if (opponentdistance < puck.getPuckSize() + opponent.getSize()){
            //They collide
            puckVelocityX = opponentPaddleVelocityX;
            puckVelocityY = opponentPaddleVelocityY;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));

            puckPoint.set(puckPoint.x, puckPoint.y);
            //puck.update(puckPoint);
        }


        // Wall collisions
        if (puckPoint.x + puck.getPuckSize() > ScreenConstants.SCREEN_WIDTH){
            puckVelocityX = -puckVelocityX;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.set(puckPoint.x, puckPoint.y);
            //puck.update(puckPoint);
        }else if(puckPoint.x - puck.getPuckSize() < 0){
            puckVelocityX = -puckVelocityX;
            puckPoint.x = (int) (puckPoint.x + (puckVelocityX));
            puckPoint.set(puckPoint.x, puckPoint.y);
            //puck.update(puckPoint);
        }else if(puckPoint.y - puck.getPuckSize() < 0){
            puckVelocityY = -puckVelocityY;
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
            puckPoint.set(puckPoint.x, puckPoint.y);
            //puck.update(puckPoint);
        }else if(puckPoint.y + puck.getPuckSize() > ScreenConstants.SCREEN_HEIGHT){
            puckVelocityY = -puckVelocityY;
            puckPoint.y = (int) (puckPoint.y + (puckVelocityY));
            puckPoint.set(puckPoint.x, puckPoint.y);
            //puck.update(puckPoint);
        }
        puck.update(puckPoint);
        puckVelocityY = (float) (puckVelocityY * 0.99);
        puckVelocityX = (float) (puckVelocityX * 0.99);
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

    public void draw(Canvas canvas){
        super.draw(canvas);
        if (canvas!= null) {
            canvas.drawColor(Color.WHITE);

            theBoard.draw(canvas);
            puck.draw(canvas);
            opponent.draw(canvas);
            player.draw(canvas);
        }
//        if(gameOver){
//
//            paint.setTextSize(100);
//            paint.setColor(Color.GREEN);
//            drawScore(canvas, paint, playerScore + " - " + opponentScore);
//        }
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
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

}