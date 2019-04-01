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

        // THREAD JUST WAITS TO HEAR "TRUE" FROM HOST AND SENDS "GET"
        final ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (JoinGameActivity.sendReceive != null) {
                    if (JoinGameActivity.sendReceive.textSent.equals("true")) {

                        JoinGameActivity.sendReceive.write("got");
                        Intent intent = new Intent(WaitingForHostActivity.this, GameActivity.class);
                        intent.putExtra("status", "client");
                        startActivity(intent);

                        scheduleTaskExecutor.shutdown();
                    }
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);


    }



}
