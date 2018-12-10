package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public class Puck implements GameObject{

    private RectF puck;
    private int puckColor;
    private int puckSize;

    public Puck(RectF puck, int puckColor, int puckSize){
        this.puck = puck;
        this.puckColor = puckColor;
        this.puckSize = puckSize;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(puckColor);
        canvas.drawOval(puck, paint);
    }

    @Override
    public void update(Point point) {
        float multiplier = ScreenConstants.getMultiplier(point);
        puck.set(point.x - puckSize * multiplier/2, point.y - puckSize*multiplier/2, point.x+puckSize*multiplier/2, point.y + puckSize*multiplier/2);

    }

    public RectF getPuck() {
        return puck;
    }

    public Boolean collides(Paddle paddle){
        float paddleRad = paddle.getPaddle().width()/2;
        float paddleX = paddle.getPaddle().centerX();
        float paddleY = paddle.getPaddle().centerY();

        double distance = Math.sqrt(Math.pow(puck.centerX() - paddleX,2)+Math.pow(puck.centerY()-paddleY,2));

        return (distance <= paddleRad +(puck.width()/2));
    }

}
