package edu.stlawu.hockeyair;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by User on 3/11/2018.
 */

//Main Thread that runs the FPS of the game
public class MainThread extends Thread {
    public static final int MAX_FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private Panel gamePanel;
    private boolean running;
    public static Canvas canvas;
    //sadsd
    public MainThread(SurfaceHolder surfaceHolder, Panel gamePanel){
        super();
        this.surfaceHolder=surfaceHolder;
        this.gamePanel=gamePanel;
    }

    //whether we want to pause or continue
    public void setRunning(boolean running){
        this.running=running;
    }

    //Runs forever with 30 frames per second.
    @Override
    public void run(){
        long startTime;
        long TimeMillis = 1000/MAX_FPS;
        long waitTime;
        int frameCount=0;
        long totalTime=0;
        long targetTime=1000/MAX_FPS;

        while(running){
            startTime=System.nanoTime();
            canvas=null;

            try{
                canvas = this.surfaceHolder.lockCanvas();

                //Does an update
                this.gamePanel.update();
                //then draws
                this.gamePanel.draw(canvas);

            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch(Exception e){e.printStackTrace();}
                }
            }

//            TimeMillis = (System.nanoTime() - startTime)/1000000;
//            waitTime = targetTime-TimeMillis;
//            try{
//                if(waitTime>0){
//                    this.sleep(waitTime);
//                }
//
//            }catch (Exception e){e.printStackTrace();}
//
//            totalTime+=System.nanoTime()-startTime;
//            frameCount++;
//            if(frameCount==MAX_FPS){
//                averageFPS=1000/((totalTime/frameCount)/1000000);
//                frameCount=0;
//                System.out.println(averageFPS);
//            }
        }

    }
}