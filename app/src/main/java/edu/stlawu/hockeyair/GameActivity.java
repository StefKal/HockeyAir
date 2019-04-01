package edu.stlawu.hockeyair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameActivity extends Activity {
    public boolean gameOver;
    Panel mPanel;
    private String status;



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
        status = getIntent().getStringExtra("status");

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                setContentView(R.layout.game_over);
                                Button bPlayAgain = findViewById(R.id.button_play_again);
                                Button bExit = findViewById(R.id.button_exit);

                                bPlayAgain.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });

                                bExit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                        moveTaskToBack(true);
                                    }
                                });
                            }
                        });


                        timerThread.interrupt();

                    }
                }
            },0,100, TimeUnit.MILLISECONDS);
        }
    });

}
