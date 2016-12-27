package me.pr3a.localweather;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import me.pr3a.localweather.Helper.UrlApi;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyCustomFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final UrlApi urlApi2 = new UrlApi();
    private static final String sid = "Ruk";
    private static final String url2 = "http://www.doofon.me/device/update/FCMtoken";

    @Override
    public void onTokenRefresh() {
        // if token expired this will get refresh token.
        String newToken = FirebaseInstanceId.getInstance().getToken();
        //sendTokenToServer(newToken);
    }

    /*
        UrlApi urlApi2 = new UrlApi();
        MyCustomFirebaseInstanceIdService.sendTokenToServer(urlApi.getApikey(), token);
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
