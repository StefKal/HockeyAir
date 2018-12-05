package edu.stlawu.hockeyair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private Button btn_create_game, btn_join_game, btn_about;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_about = findViewById(R.id.about_btn);
        btn_join_game = findViewById(R.id.join_game);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setClickListeners();

    }



    private void setClickListeners(){


        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.about);
                builder.setMessage(R.string.about_txt);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.show();

            }
        });



        btn_join_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JoinGameActivity.class));
            }
        });



    }
}
