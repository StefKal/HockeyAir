package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public class Paddle implements GameObject {


    private RectF paddle;
    private RectF handle;
    private int paddleColor;
    private int handleColor;


    private float paddleSize;
    private float handleSize;

    public Paddle(RectF paddle, int paddleSize, int paddleColor,  int handleColor ) {
        this.paddle = paddle;
        this.paddleSize = paddleSize;
        this.handleColor = handleColor;
        this.paddleColor = paddleColor;
        handle = new RectF(paddle.left/2, paddle.top/2, paddle.right/2, paddle.bottom/2);
        handleSize = paddleSize/4;

    }

    public float getSize()
    {
        return paddleSize;
    }

    public RectF getPaddle() {
        return paddle;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(paddleColor);
        canvas.drawOval(paddle, paint);
        paint.setColor(handleColor);
        canvas.drawOval(handle, paint);
    }

    @Override
    public void update(Point point) {
        paddle.set(point.x - paddleSize , point.y - paddleSize, point.x+paddleSize, point.y + paddleSize);
        handle.set(point.x - handleSize, point.y - handleSize, point.x+handleSize, point.y + handleSize);

    }
}
