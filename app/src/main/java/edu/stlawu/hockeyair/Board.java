package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class Board implements GameObject {

    private Path playerBoard;
    private Path opponentBoard;
    private Path fullBoard;
    private Path playerGoal;
    private Path opponentGoal;
    //test

    private int boardWidth;
    private int boardHeight;


    // topLeft
    private int topLeftX = 0;
    private int topLeftY = 0;

    // topRight
    private int topRightX = ScreenConstants.SCREEN_WIDTH;
    private int topRightY = 0;

    // botLeft
    private int botLeftX = 0;
    private int botLeftY = ScreenConstants.SCREEN_HEIGHT;

    // botRight
    private int botRightX = ScreenConstants.SCREEN_WIDTH;
    private int botRightY = ScreenConstants.SCREEN_HEIGHT;

    // centerPoints
    private int yCenter = ScreenConstants.SCREEN_HEIGHT/2;

    private int boardColor;

    public Board(int boardColor){
        this.boardColor = boardColor;

        this.fullBoard = makeRectanglePath(topLeftX, topLeftY, topRightX, topRightY, botLeftX, botLeftY, botRightX, botRightY);

        this.playerBoard = makeRectanglePath(topLeftX, yCenter, topRightX, yCenter, botLeftX, botLeftY, botRightX, botRightY);
        this.opponentBoard = makeRectanglePath(topLeftX, topLeftY, topRightX, topRightY, botLeftX, yCenter, botRightX, yCenter);

        //this.playerGoal = makeRectanglePath();
        //this.opponentGoal = makeRectanglePath();

        this.boardWidth = ScreenConstants.SCREEN_WIDTH;
        this.boardHeight = ScreenConstants.SCREEN_HEIGHT;



    }

    private float getDistance(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(((x2 - x1) * (x2 -x1)) + ((y2 - y1) * (y2 - y1)));
    }

    //draws the board
    private Path makeRectanglePath(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){
        Path rectangle = new Path();

        float left = getDistance(x1, y1,x3,y3);
        float top = getDistance(x1,y1, x2, y2);
        float right = getDistance(x2, y2, x4, y4);
        float bot = getDistance(x3, y3, x4, y4);

        rectangle.addRect(left, top, right, bot, Path.Direction.CW);

        return rectangle;
    }

    // draws using the canvas
    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(boardColor);
        canvas.drawPath(playerGoal,paint);
        canvas.drawPath(opponentBoard,paint);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(15);
        canvas.drawText("FUCK THAT",ScreenConstants.SCREEN_WIDTH/2, yCenter, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        paint.setColor(Color.BLACK);


    }

    @Override
    public void update(Point point) {

    }

/*
    @Override
    public void update(Point point) {

    }
    public int scoredGoal(Puck puck) {

        // board screen
        Region board = new Region(0, 0, ScreenConstants.SCREEN_WIDTH, ScreenConstants.SCREEN_HEIGHT);

        // create paths
        Path goal = new Path();
        Path oppGoal = new Path();
        Path hockeyPuck = new Path();

        // draw rect for goal and puck to check intersect
        goal.addRect(playerGoal,Path.Direction.CCW); // CCW Counter Clockwise
        oppGoal.addRect(opponentGoal, Path.Direction.CCW);
        hockeyPuck.addOval(puck.getPuck(), Path.Direction.CCW);

        Region region_goal=new Region();
        Region region_hockeyPuck=new Region();
        Region region_oppGoal = new Region();

        // create region paths
        region_goal.setPath(goal,board);
        region_hockeyPuck.setPath(hockeyPuck,board);
        region_oppGoal.setPath(oppGoal,board);


        // check for intersect and return which goals
        if(region_goal.op(region_hockeyPuck, Region.Op.INTERSECT)){

            return MYGOAL;

        }
        else if(region_oppGoal.op(region_hockeyPuck, Region.Op.INTERSECT)){

            return OPPONENTGOAL;

        } else {

            return 0;

        }


    }

    // puck in goal?
    public boolean goalTouch(Puck puck){
        return scoredGoal(puck)!= 0;
    }

    // check if the puck is still on the board
    public String containsPuck(Puck puck){
        return collisionPos(fullBoard,puck.getPuck());
    }

    //check if paddle is still on the board
    public String containsPaddle(Paddle paddle){
        return collisionPos(myBoard,paddle.getPaddle());
    }

    // determine the pos of the puck (my side or theirs)
    public boolean puckOnMyBoard(int X, int Y){
        Region board = new Region(0, 0, ScreenConstants.SCREEN_WIDTH, ScreenConstants.SCREEN_HEIGHT);
        Region region = new Region();
        region.setPath(myBoard, board);
        return region.contains(X,Y);
    }

    //get pos of puck and paddle collision
    private String collisionPos(Path path,RectF obj) {
        Region board = new Region(0, 0, ScreenConstants.SCREEN_WIDTH, ScreenConstants.SCREEN_HEIGHT);

        Path circle = new Path();
        circle.addRect(obj, Path.Direction.CCW); // counter clock wise 

        Region region = new Region();
        region.setPath(path, board);

        int xleft = (int) (obj.centerX() - obj.width() / 2);
        int yleft = (int) (obj.centerY());

        int xtop = (int) (obj.centerX());
        int ytop = (int) (obj.centerY() - obj.height() / 2);

        int xright = (int) (obj.centerX() + obj.width() / 2);
        int yright = (int) (obj.centerY());

        int xbottom = (int) (obj.centerX());
        int ybottom = (int) (obj.centerY() + obj.height() / 2);

        if (!region.contains(xleft, yleft) && !region.contains(xtop,ytop)) {
            return "left-top";
        } else if (!region.contains(xleft, yleft) && !region.contains(xbottom,ybottom)) {
            return "left-bottom";
        } else if (!region.contains(xright, yright) && !region.contains(xtop,ytop)) {
            return "right-top";
        } else if (!region.contains(xright, yright) && !region.contains(xbottom,ybottom)) {
            return "right-bottom";
        } else if(!region.contains(xright,yright)){
            return "right";
        }else if(!region.contains(xleft,yleft)){
            return "left";
        } else if (!region.contains(xbottom,ybottom)){
            return "bottom";
        }else if (!region.contains(xtop,ytop)){
            return "top";
        } else {
            return null;
        }


    }
*/
}
