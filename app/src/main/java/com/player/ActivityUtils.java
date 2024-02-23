package com.player;

import android.app.ActionBar;
import android.view.View;

import androidx.activity.ComponentActivity;

public class ActivityUtils {

    public static void hideStatusBar( ComponentActivity a )
    {
        View decorView = a.getWindow().getDecorView();

        // https://developer.android.com/training/system-ui/immersive#sticky
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if ( decorView != null ) {
            decorView.setSystemUiVisibility(uiOptions);
        }

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar bar = a.getActionBar();
        if ( bar != null )
        {
            bar.hide();
        }
    }

}