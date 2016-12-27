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

    private static final UrlApi urlApi2 = new UrlApi();
    private static final String sid = "Ruk";
    private static final String url2 = "http://www.doofon.me/device/update/FCMtoken";

    /**
     * Checks if the device is connected to the network.
     *
     * @param activity to activity to be used
     * @return if a network is available return true, otherwise false
     */
    public static boolean isNetworkConnected(Activity activity) {
        /*ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null) {
                if (netInfos.isConnected()) {
                    return true;
                }
            }
        }
        return false;
        */

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    /*
       UrlApi urlApi2 = new UrlApi();
       String token = FirebaseInstanceId.getInstance().getToken();
       MyNetwork.updateToken(urlApi2.getApikey(), token);
    */
    // send new FCMtoken to your server.
    public static void sendTokenToServer(String apiKey, String token) {
        Log.d("updateToken", "=" + apiKey);
        Log.d("updateToken", "=" + token);
        urlApi2.setUri(url2, apiKey);
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("SerialNumber", apiKey + "")
                    .add("FCMtoken", token + "")
                    .add("sid", sid)
                    .build();
            Request request = new Request.Builder()
                    .url(urlApi2.getUrl())
                    .post(formBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                }
            });
            //Toast.makeText(this, "UPDATE Token", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
