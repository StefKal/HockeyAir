package edu.stlawu.hockeyair;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    Panel mPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get who called this activity
        String status = getIntent().getStringExtra("status");
        initialize();

        mPanel = new Panel(this, status);
        setContentView(mPanel);
    }

    private void initialize(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ScreenConstants.SCREEN_WIDTH = dm.widthPixels;
        ScreenConstants.SCREEN_HEIGHT = dm.heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(mPanel);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
