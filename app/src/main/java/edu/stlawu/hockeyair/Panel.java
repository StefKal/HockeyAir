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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@SuppressLint("ViewConstructor")
public class Panel extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, Runnable {

    private GameActivity gameActivity = (GameActivity) getContext();

    private String status;


    private static int PLAYER_WINS = 1;
    private static int OPPONENT_WINS = -1;
    private static int DRAW = 2;
    private static int STILL_PLAYING = 0;

    static boolean gameOver = false;

    private int timer;

    private RectF playerGoal;
    private RectF opponentGoal;

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

        playerGoal = makeRectanglePath((ScreenConstants.SCREEN_WIDTH/2)-(ScreenConstants.SCREEN_WIDTH/4), ScreenConstants.SCREEN_HEIGHT-20,
                (ScreenConstants.SCREEN_WIDTH/2)+(ScreenConstants.SCREEN_WIDTH/4), ScreenConstants.SCREEN_HEIGHT+20);

        opponentGoal =  makeRectanglePath((ScreenConstants.SCREEN_WIDTH/2)-(ScreenConstants.SCREEN_WIDTH/4),
                -20, (ScreenConstants.SCREEN_WIDTH/2)+(ScreenConstants.SCREEN_WIDTH/4), +20);



        playerPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, 3*ScreenConstants.SCREEN_HEIGHT/4);
        opponentPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/4 );
        puckPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/2);
        
        player.update(playerPoint);
        opponent.update(opponentPoint);
        puck.update(puckPoint);


        timer = 1000;

        puckVelocityX = 0;
        puckVelocityY = 0;
        playerPaddleVelocityX = 0;
        playerPaddleVelocityY = 0;
        opponentPaddleVelocityX = 0;
        opponentPaddleVelocityY = 0;
        sendCoordinates.start();
        timerThread.start();

    }


    //gameLoop
    public void update(){

        if (timer == 0) {
            gameOver = true;
        }

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
    public int whoWon(){
        if (gameOver && playerScore > opponentScore ){//|| opponentScore == CustomizeGameActivity.int_round_num){
            gameActivity.gameOver = true;
            timer =0;
            timerThread.interrupt();
            return PLAYER_WINS;

        }else if(gameOver && playerScore < opponentScore){ // || playerScore == CustomizeGameActivity.int_round_num) {
            gameActivity.gameOver = true;
            timer = 0;
            timerThread.interrupt();

            return OPPONENT_WINS;
        }else if(gameOver && playerScore == opponentScore  ){
            gameActivity.gameOver = true;
            timer = 0;
            timerThread.interrupt();
            return DRAW;
        }else{
            return STILL_PLAYING;
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

        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        playerPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, 3*ScreenConstants.SCREEN_HEIGHT/4 );
        opponentPoint = new Point(ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/4 );

        player.update(playerPoint);
        opponent.update(opponentPoint);

        puckPoint = new Point(ScreenConstants.SCREEN_WIDTH/2,ScreenConstants.SCREEN_HEIGHT/2);
        puck.update(puckPoint);

        oldX=ScreenConstants.SCREEN_WIDTH/2;
        oldY=3*ScreenConstants.SCREEN_HEIGHT/4;

        puckVelocityX=0;
        puckVelocityY=0;

    }




    //draws the board
    private RectF makeRectanglePath(float left, float top, float right, float bot){
        RectF rectangle;

        rectangle = new RectF(left, top, right, bot);

        return rectangle;
    }

    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final Paint paint = new Paint();
        canvas.drawColor(Color.WHITE);

        canvas.drawRect(playerGoal, paint);
        canvas.drawRect(opponentGoal, paint);

        theBoard.draw(canvas);
        puck.draw(canvas);
        opponent.draw(canvas);
        player.draw(canvas);

        paint.setColor(Color.WHITE);

        paint.setColor(Color.WHITE);
        paint.setTextSize(80);

        int tens = timer % 10;
        int seconds = (timer / 10) % 60;
        int minutes = (timer / 600) % 60;

        @SuppressLint("DefaultLocale") String mTimer = String.format("%02d:%02d:%d",minutes, seconds, tens);

        canvas.drawText(mTimer, ScreenConstants.SCREEN_WIDTH -300, 70, paint);



        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(80);
        Rect rect = new Rect();
        canvas.getClipBounds(rect);

        canvas.drawText(String.valueOf(opponentScore), 200, 200, paint);
        canvas.drawText(String.valueOf(playerScore), ScreenConstants.SCREEN_WIDTH - 280, ScreenConstants.SCREEN_HEIGHT - 200, paint);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.DKGRAY);

        if (whoWon() == PLAYER_WINS){
            canvas.drawText("CONGRATULATIONS YOU WON", ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/2, paint);
        }else if(whoWon() == OPPONENT_WINS) {
            canvas.drawText("BETTER LUCK NEXT TIME", ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/2, paint);
        }else if(whoWon() == DRAW){
            canvas.drawText("WELL THAT'S AWKWARD :|", ScreenConstants.SCREEN_WIDTH/2, ScreenConstants.SCREEN_HEIGHT/2, paint);
        }else if(whoWon() == STILL_PLAYING){

        }
    }

//    public void drawScore(Canvas canvas, Paint paint, String score){
//        paint.setTextAlign(Paint.Align.LEFT);
//        paint.setTextSize(80);
//        Rect rect = new Rect();
//        canvas.getClipBounds(rect);
//
//
//        paint.getTextBounds(score, 0, score.length(), rect);
//        canvas.drawText(score, 200, 200, paint);
//        canvas.drawText(score, ScreenConstants.SCREEN_WIDTH - 280, ScreenConstants.SCREEN_HEIGHT - 200, paint);
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //mainThread = new MainThread(getHolder(),this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }//

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

    public void checkScore(){
        if (puck.getPuck().intersect(playerGoal)) {
            goal();
            opponentScore += 1;
        }

        else if (puck.getPuck().intersect(opponentGoal)) {
            goal();
            playerScore += 1;
        }//
    }


    @Override
    public void run() {
        while(isRunning){
            myHolder = getHolder();

            if(!myHolder.getSurface().isValid())
                continue;

            Canvas canvas = myHolder.lockCanvas();
            update();
            checkScore();
            ballIntersectUpdate();
            whoWon();
            if (canvas!= null) {
                draw(canvas);
                myHolder.unlockCanvasAndPost(canvas);
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
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
                    if(newY < ScreenConstants.SCREEN_HEIGHT/2 + player.getSize())
                        newY = ScreenConstants.SCREEN_HEIGHT/2 + player.getSize();
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

    final ScheduledExecutorService task = Executors.newScheduledThreadPool(2);

    Thread timerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            task.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    timer--;
                }
            },0,100, TimeUnit.MILLISECONDS);
        }
    });

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
            }, 0, 10, TimeUnit.MILLISECONDS);



        }
    });
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

}