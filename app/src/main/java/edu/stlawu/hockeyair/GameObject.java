package edu.stlawu.hockeyair;


import android.graphics.Canvas;
import android.graphics.Point;

//Interface shared by all objects that can be drawn
public interface GameObject {
    void draw(Canvas canvas);
    void update(Point point);
}
