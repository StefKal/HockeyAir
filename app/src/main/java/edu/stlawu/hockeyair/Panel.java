package edu.stlawu.hockeyair;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private boolean gameOver=false;

    private Board theBoard;
    private Paddle player;
    private Puck puck;

    private Point playerPoint;
    private Point puckPoint;

    private Paddle opponent;
    private Point opponentPoint;

    float [] history = new float[2];
    double oldX;
    double oldY;

    double myPaddleOldX;
    double myPaddleOldY;


    int playerPaddleVelocityX;
    int playerPaddleVelocityY;

    int opponentPaddleVelocityX;
    int opponentPaddleVelocityY;

    int puckVelocityX;
    int puckVelocityY;

    int playerScore;
    int opponentScore;

    public Panel(Context context){
        super(context);

        theBoard = new Board(Color.MAGENTA);
        player = new Paddle(new RectF(100,100,
                (ScreenConstants.SCREEN_WIDTH+ScreenConstants.SCREEN_WIDTH)/9,
                (ScreenConstants.SCREEN_WIDTH+ScreenConstants.SCREEN_WIDTH)/9),
                10,  Color.RED,Color.BLACK);
        opponent= new Paddle(new RectF(100,100,
                (ScreenConstants.SCREEN_WIDTH+ScreenConstants.SCREEN_WIDTH)/9,
                (ScreenConstants.SCREEN_WIDTH+ScreenConstants.SCREEN_WIDTH)/9),
                10, Color.RED,Color.BLACK);
        puck = new Puck(new RectF(100,100,
                (ScreenConstants.SCREEN_WIDTH+ScreenConstants.SCREEN_WIDTH)/10,
                (ScreenConstants.SCREEN_WIDTH+ScreenConstants.SCREEN_WIDTH)/10),
                10, Color.WHITE);

        playerScore = 0;
        opponentScore = 0;

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

    //checks for intercepts with the goal
    public void goalIntersectUpdate(){
        if(theBoard.goalTouch(puck)){
            if(theBoard.scoredGoal(puck)==Board.MYGOAL)opponentScore++;
            else playerScore++;
            gameOver=true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
}
