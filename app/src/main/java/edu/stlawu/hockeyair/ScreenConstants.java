package edu.stlawu.hockeyair;

import android.graphics.Point;


public class ScreenConstants {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static float getMultiplier(Point point){
        return (float)((0.4/ScreenConstants.SCREEN_HEIGHT)*point.y + 0.6);
    }
}
