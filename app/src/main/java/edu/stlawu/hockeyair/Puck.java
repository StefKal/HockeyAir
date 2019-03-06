package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public class Puck implements GameObject{

    private RectF puck;
    private int puckColor;
    private int puckSize;

    Puck(RectF puck, int puckColor, int puckSize){
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
        puck.set(point.x - puckSize , point.y - puckSize, point.x+puckSize, point.y + puckSize);

    }

    public RectF getPuck() {
        return puck;
    }
    public int getPuckSize() {
        return puckSize;
    }

}
