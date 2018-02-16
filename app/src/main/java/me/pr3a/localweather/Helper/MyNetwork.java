package me.pr3a.localweather.Helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyNetwork {

    public final static String URLDEVICE = "http://192.168.44.51/DooFon/public/api/device/";
    public final static String URLWEATHER = "http://192.168.44.51/DooFon/public/api/weather/";
    public final static String URLLOCATION = "http://192.168.44.51/DooFon/public/api/device/update/location";
    public final static String URLFCMTOKEN = "http://192.168.44.51/DooFon/public/api/device/update/FCMtoken";
    public final static String URLTHRESHOLD = "http://192.168.44.51/DooFon/public/api/device/update/threshold";

    /**
     * Checks if the device is connected to the network.
     *
     * @param activity to activity to be used
     * @return if a network is available return true, otherwise false
     */
    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
