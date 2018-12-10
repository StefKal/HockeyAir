package edu.stlawu.hockeyair;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class Panel extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private String status;

    private MainThread mainThread;

    private boolean gameOver=false;

    private Board theBoard;
    private Paddle player;
    private Puck puck;

    private Point playerPoint;
    private Point puckPoint;

    private Paddle opponent;
    private Point opponentPoint;

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
    int playerCollisionMultiplier=0;
    int opponentCollisionMultipler=0;


    int playerScore;
    int opponentScore;

    public Panel(Context context, String status){
        super(context);
        mainThread = new MainThread(getHolder(), this);
        mainThread.setRunning(true);

        mainThread.start();
        this.status = status;
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
        canvas.drawColor(Color.WHITE);

        theBoard.draw(canvas);
        puck.draw(canvas);
        opponent.draw(canvas);
        player.draw(canvas);

        if(gameOver){
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.GREEN);
            drawScore(canvas, paint, playerScore + " -- " + opponentScore);
        }
    }

    public void drawScore(Canvas canvas, Paint paint, String score){
        paint.setTextAlign(Paint.Align.LEFT);
        Rect rect = new Rect();
        canvas.getClipBounds(rect);
        int canvasHeight = rect.height();
        int canvasWidth = rect.width();

        paint.getTextBounds(score, 0, score.length(), rect);
        float x = (float) (canvasHeight / 2.0 - rect.height() / 2.0 - rect.bottom);
        float y = (float) (canvasWidth / 2.0 + rect.width() / 2.0 - rect.left);
        canvas.drawText(score, x, y, paint);
    }

    // drag and drop

    private void updatePoint(float dx, float dy){
        oldX = playerPoint.x;
        oldY = playerPoint.y;
        playerPoint.set((int)(oldX+dx*8),(int)( oldY+dy*8));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mainThread = new MainThread(getHolder(),this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        while(true){
            try{
                mainThread.setRunning(false);
                mainThread.join();

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
}
