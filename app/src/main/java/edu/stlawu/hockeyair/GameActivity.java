package edu.stlawu.hockeyair;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    private String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get who called this activity
        this.status = getIntent().getStringExtra("status");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ScreenConstants.SCREEN_WIDTH = dm.widthPixels;
        ScreenConstants.SCREEN_HEIGHT = dm.heightPixels;
        setContentView(new Panel(this, status));


    }
}
