package me.pr3a.localweather.Helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
