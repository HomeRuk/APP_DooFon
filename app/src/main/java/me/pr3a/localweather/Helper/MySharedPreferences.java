package me.pr3a.localweather.Helper;


import android.content.SharedPreferences;

public class MySharedPreferences {

    // Read SerialNumber
    private void getPreference(SharedPreferences mPreferences) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.remove("Serial");
            editor.apply();
    }
}
