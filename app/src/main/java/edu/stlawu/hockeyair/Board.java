package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;

public class Board implements GameObject {

    static int MYGOAL = 1;
    static int OPPONENTGOAL = 2;

    // static vars for collisionPos
    static int LEFT_TOP = 10;
    static int RIGHT_TOP = 11;
    static int RIGHT_BOTTOM = 12;
    static int RIGHT = 13;
    static int LEFT = 14;
    static int BOTTOM = 15;
    static int TOP = 16;

    private Path myBoard;
    private Path opponentBoard;
    private Path fullBoard;

    private RectF myGoal;
    private RectF opponentGoal;

    private int boardColor;

    private float xTopRight;
    private float xTopLeft;

    private float xInterceptLeft;
    private float xInterceptRight;

    private float xCenter;
    private float yCenter;

    public Board(int boardColor){

        this.boardColor = boardColor;
        xTopLeft = ScreenConstants.SCREEN_WIDTH;
        xTopRight = ScreenConstants. SCREEN_WIDTH;
        //getCenter();

        fullBoard=drawRectangle(0,ScreenConstants.SCREEN_HEIGHT,
                ScreenConstants.SCREEN_WIDTH,
                ScreenConstants.SCREEN_HEIGHT,
                xTopRight,0,xTopLeft,0);

        myBoard = drawRectangle(0,ScreenConstants.SCREEN_HEIGHT,ScreenConstants.SCREEN_WIDTH,ScreenConstants.SCREEN_HEIGHT,
                xInterceptRight,yCenter,xInterceptLeft,yCenter);

        opponentBoard=drawRectangle(xInterceptLeft,yCenter,xInterceptRight,yCenter,
                xTopRight,0,xTopLeft,0);

        myGoal= new RectF((ScreenConstants.SCREEN_WIDTH/2)-
                (ScreenConstants.SCREEN_WIDTH/4),
                ScreenConstants.SCREEN_HEIGHT-20,
                (ScreenConstants.SCREEN_WIDTH/2)+(ScreenConstants.SCREEN_WIDTH/4),
                ScreenConstants.SCREEN_HEIGHT+20);

        double multiplier = ScreenConstants.getMultiplier(new Point(0,0));
        opponentGoal = new RectF((ScreenConstants.SCREEN_WIDTH/2)-((int)(multiplier*ScreenConstants.SCREEN_WIDTH/4)),
                -20, (ScreenConstants.SCREEN_WIDTH/2)+((int)(multiplier*ScreenConstants.SCREEN_WIDTH/4)),
                +20);


//        decoration=new RectF(xCenter-ScreenConstants.SCREEN_WIDTH/8,
//                yCenter-ScreenConstants.SCREEN_HEIGHT/9,
//                xCenter +ScreenConstants.SCREEN_WIDTH/6,yCenter+ScreenConstants.SCREEN_HEIGHT/10);
//
    }


    //draws a trapezoid(boards)
    public Path drawRectangle(float x1,float y1,float x2,float y2,float x3,float y3, float x4, float y4){
        Path rectangle = new Path();
        rectangle.reset();

        rectangle.moveTo(x1,y1);
        rectangle.lineTo(x2,y2);
        rectangle.lineTo(x3,y3);
        rectangle.lineTo(x4,y4);
        rectangle.lineTo(x1,y1);

        return rectangle;
    }
    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(boardColor);
        canvas.drawPath(myBoard, paint);
        canvas.drawPath(opponentBoard, paint);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(15);
        canvas.drawLine(xInterceptLeft, yCenter, xInterceptRight,yCenter, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        //canvas.drawOval(decoration, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(myGoal, paint);
        canvas.drawRect(opponentGoal, paint);


    }


    @Override
    public void update(Point point) {

    }

    // gets center of screen by returning yCenter
    public double getCenterY(){
        return yCenter;
    }

    //gets center view
    private void getCenter(){
        double slope_one = ScreenConstants.SCREEN_HEIGHT / -xTopRight;
        double slope_two = ScreenConstants.SCREEN_HEIGHT / (ScreenConstants.SCREEN_WIDTH-xTopLeft);

        double B1 = (double)ScreenConstants.SCREEN_HEIGHT;
        double B2 = (double)ScreenConstants.SCREEN_HEIGHT - (slope_two * ScreenConstants.SCREEN_WIDTH);

        xCenter = (float)((B2-B1) / (slope_one - slope_two));
        yCenter = (float)((xCenter * slope_one) + ScreenConstants.SCREEN_HEIGHT);

        System.out.println("xCenter: " + xCenter);
        System.out.println("XLength: " + ScreenConstants.SCREEN_WIDTH);

        // get intercepts
        getIntercepts();
    }

    // get intercepts
    private void getIntercepts(){
        double length_one = xTopRight - xTopLeft;
        double length_two = ScreenConstants.SCREEN_WIDTH;

        double midLength =(2 * length_one * length_two) / (length_one + length_two);

        xInterceptLeft =(float)(xCenter - midLength/2);
        xInterceptRight =(float)(xCenter + midLength/2);

    }

    // check if goal was scored and which goal it was scored on
    public int scoredGoal(Puck puck) {

        // board screen
        Region board = new Region(0, 0, ScreenConstants.SCREEN_WIDTH, ScreenConstants.SCREEN_HEIGHT);

        // create paths
        Path goal = new Path();
        Path oppGoal = new Path();
        Path hockeyPuck = new Path();

        // draw rect for goal and puck to check intersect
        goal.addRect(myGoal,Path.Direction.CCW); // CCW Counter Clockwise
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

}
