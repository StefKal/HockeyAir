package edu.stlawu.hockeyair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class WaitingForHostActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_for_host);

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (JoinGameActivity.sendReceive.textSent.equals("True")) {
                    startActivity(new Intent(WaitingForHostActivity.this, GameActivity.class));
                }
            }
        }, 0, 5, TimeUnit.SECONDS);


    }



}
