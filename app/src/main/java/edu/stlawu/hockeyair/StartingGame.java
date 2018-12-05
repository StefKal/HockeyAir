package edu.stlawu.hockeyair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartingGame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_game_layout);
        startActivity(new Intent(StartingGame.this, AndroidLauncher.class));
    }
}
