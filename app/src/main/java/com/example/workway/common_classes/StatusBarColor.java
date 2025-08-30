package com.example.workway.common_classes;

import android.app.Activity;
import android.os.Build;
import com.example.workway.R;

public class StatusBarColor {

    public static void ChangeColor(Activity act){//To change the status bar background color...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            act.getWindow().setStatusBarColor(act.getResources().getColor(R.color.ProgressBarColor, act.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            act.getWindow().setStatusBarColor(act.getResources().getColor(R.color.ProgressBarColor));
        }
    }
}
