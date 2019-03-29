package edu.stlawu.hockeyair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomizeGameActivity extends Activity {

    public Button submit;
    public List myList;

    private EditText puck_size, puck_speed, goal_size, goal_num, time;
    public static int int_puck_size, int_puck_speed, int_goal_size, int_goal_num, int_time;
    private TextView required_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutomize_game);

        initialize();


    }


    private void initialize(){
        submit = findViewById(R.id.submit_btn);
        required_text = findViewById(R.id.required_fields);
        puck_size = findViewById(R.id.edit_puck_size);
        puck_speed = findViewById(R.id.edit_puck_speed);
        goal_size = findViewById(R.id.edit_goal_size);
        goal_num = findViewById(R.id.edit_rounds);
        time = findViewById(R.id.edit_time);
    }


    private boolean check_empty(EditText aText){
        return TextUtils.isEmpty(aText.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (check_empty(puck_size) || check_empty(puck_speed) || check_empty(goal_size) || check_empty(goal_num) || check_empty(time)){
                    required_text.setText("Please fill in all text fields");
                }else {

                    int_puck_size = Integer.parseInt(puck_size.getText().toString());
                    int_puck_speed = Integer.parseInt(puck_speed.getText().toString());
                    int_goal_size = Integer.parseInt(goal_size.getText().toString());
                    int_goal_num = Integer.parseInt(goal_num.getText().toString());
                    int_time = Integer.parseInt(time.getText().toString());

                    submit.setEnabled(false);
                    writeThread.start();



                }
            }
        });
    }


    // THREAD THAT SENDS "TRUE" TO CLIENT, WAITS FOR "GOT" TO START GAME ACTIVITY
    final ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
    Thread writeThread = new Thread(new Runnable() {
        @Override
        public void run() {
            JoinGameActivity.sendReceive.write("true");

            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if(JoinGameActivity.sendReceive.textSent.equals("got")){
                        Intent intent = new Intent(CustomizeGameActivity.this, GameActivity.class);
                        intent.putExtra("status", "host");
                        startActivity(intent);
                        scheduleTaskExecutor.shutdown();

                    }
                }
            }, 0, 1000, TimeUnit.MILLISECONDS);

        }
    });

}
