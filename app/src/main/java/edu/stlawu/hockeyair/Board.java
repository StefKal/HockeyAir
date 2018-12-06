package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

public class Board implements GameObject {

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

    float xCenter;
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
}
