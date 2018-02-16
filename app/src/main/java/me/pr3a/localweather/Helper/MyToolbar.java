package me.pr3a.localweather.Helper;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

@SuppressLint("Registered")
public class MyToolbar extends AppCompatActivity {

    // Show Toolbar
    public void showToolbar(String title, String subTitle, Toolbar toolbar) {
        toolbar.setTitle(title);
        toolbar.setSubtitle(subTitle);
    }
}
