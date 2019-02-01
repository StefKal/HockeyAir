package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.renderscript.ScriptC;

public class Board implements GameObject {

    private Path playerBoard;
    private Path opponentBoard;
    private Path fullBoard;
    private Path playerGoal;
    private Path opponentGoal;

    private RectF centerCirlce;
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

        this.fullBoard = makeRectanglePath(0, 0, ScreenConstants.SCREEN_WIDTH, ScreenConstants.SCREEN_HEIGHT);

        this.playerBoard = makeRectanglePath(0, 0, ScreenConstants.SCREEN_WIDTH, yCenter);
        this.opponentBoard = makeRectanglePath(0, yCenter, ScreenConstants.SCREEN_WIDTH, ScreenConstants.SCREEN_HEIGHT);

        //this.playerGoal = makeRectanglePath();
        //this.opponentGoal = makeRectanglePath();

        this.boardWidth = ScreenConstants.SCREEN_WIDTH;
        this.boardHeight = ScreenConstants.SCREEN_HEIGHT;

        playerGoal = makeRectanglePath((ScreenConstants.SCREEN_WIDTH/2)-(ScreenConstants.SCREEN_WIDTH/4), ScreenConstants.SCREEN_HEIGHT-20,
                (ScreenConstants.SCREEN_WIDTH/2)+(ScreenConstants.SCREEN_WIDTH/4), ScreenConstants.SCREEN_HEIGHT+20);

        opponentGoal = makeRectanglePath((ScreenConstants.SCREEN_WIDTH/2)-(ScreenConstants.SCREEN_WIDTH/4),
                -20, (ScreenConstants.SCREEN_WIDTH/2)+(ScreenConstants.SCREEN_WIDTH/4), +20);

        centerCirlce = new RectF((ScreenConstants.SCREEN_WIDTH/2)- 200, ScreenConstants.SCREEN_HEIGHT/2 - 200,
                (ScreenConstants.SCREEN_WIDTH/2) + 200 , yCenter + 200);







    }

    private float getDistance(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(((x2 - x1) * (x2 -x1)) + ((y2 - y1) * (y2 - y1)));
    }

    //draws the board
    private Path makeRectanglePath(float left, float top, float right, float bot){
        Path rectangle = new Path();

        rectangle.addRect(left, top, right, bot, Path.Direction.CW);

        return rectangle;
    }

    // draws using the canvas
    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(182,119,33));
        canvas.drawPath(opponentBoard,paint);
        paint.setColor(Color.rgb(127,84,23));
        canvas.drawPath(playerBoard,paint);
        paint.setColor(Color.WHITE);
        canvas.drawPath(playerGoal, paint);
        canvas.drawPath(opponentGoal, paint);
        canvas.drawArc(centerCirlce, 0, 360, false, paint);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(50);
        canvas.drawLine(0, yCenter, ScreenConstants.SCREEN_WIDTH, yCenter, paint);




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
