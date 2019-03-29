package edu.stlawu.hockeyair;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameActivity extends Activity {
    public boolean gameOver;
    Panel mPanel;



    public View game_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get who called this activity

        initialize();


    }

    private void initialize(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ScreenConstants.SCREEN_WIDTH = dm.widthPixels;
        ScreenConstants.SCREEN_HEIGHT = dm.heightPixels;
        String status = getIntent().getStringExtra("status");
        //game_over = findViewById(R.id.game_over);
        mPanel = new Panel(this, status);
        setContentView(mPanel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerThread.start();
        gameOver = Panel.gameOver;

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    final ScheduledExecutorService task = Executors.newScheduledThreadPool(1);

    Thread timerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            task.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (gameOver){
                        Log.e("G", "OTHERE");
                        setContentView(game_over);

                        //TextView winView = game_over.findViewById(R.id.textView3);
//                        if (mPanel.whoWon() == 1) {
//                            winView.setText("CONGRATULATIONS YOU WON");
//                        }else{
//                            winView.setText("TOO BAD");
//                        }

                    }
                }
            },0,100, TimeUnit.MILLISECONDS);
        }
    });

}