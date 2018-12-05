package edu.stlawu.hockeyair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomizeGameActivity extends Activity {

    public Button submit;
    public List myList;
    private EditText puck_size, puck_speed, goal_size, rnd_num, time;
    private TextView required_text;






    public CustomizeGameActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutomize_game);

        submit = findViewById(R.id.submit_btn);

        required_text = findViewById(R.id.required_fields);

        puck_size = findViewById(R.id.edit_puck_size);
        puck_speed = findViewById(R.id.edit_puck_speed);
        goal_size = findViewById(R.id.edit_goal_size);
        rnd_num = findViewById(R.id.edit_rounds);
        time = findViewById(R.id.edit_time);
        myList = new ArrayList();



    }


    private boolean check_empty(EditText aText){
        if (TextUtils.isEmpty(aText.toString()))
            return true;
        else{
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_empty(puck_size) || check_empty(puck_speed) || check_empty(goal_size) || check_empty(rnd_num) || check_empty(time)){
                    required_text.setText("Please fill in all text fields");
                }else {
//                    myList.set(0, (Integer.getInteger(puck_size.toString())));
//                    myList.set(1, (Integer.getInteger(puck_speed.toString())));
//                    myList.set(2, (Integer.getInteger(goal_size.toString())));
//                    myList.set(3, (Integer.getInteger(rnd_num.toString())));
//                    myList.set(4, (Integer.getInteger(time.toString())));
                    writeThread.start();



                }
            }
        });
    }

    Thread writeThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String msg = "True";
            JoinGameActivity.sendReceive.write(msg.getBytes());
            startActivity(new Intent(CustomizeGameActivity.this, AndroidLauncher.class));
        }
    });

}
