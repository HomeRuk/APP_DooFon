package me.pr3a.localweather;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.pr3a.localweather.Helper.UrlApi;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static me.pr3a.localweather.Helper.MyNetwork.URLFCMTOKEN;

public class MyCustomFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final UrlApi urlApi2 = new UrlApi();
    private static final String sid = "Ruk";

    @Override
    public void onTokenRefresh() {
        // if token expired this will get refresh token.
        String newToken = FirebaseInstanceId.getInstance().getToken();
        MyCustomFirebaseInstanceIdService.sendTokenToServer(urlApi2.getApikey(), newToken);
    }

    /*
        UrlApi urlApi2 = new UrlApi();
        MyCustomFirebaseInstanceIdService.sendTokenToServer(urlApi.getApikey(), token);
     */
    // send new FCMtoken to your server.
    public static void sendTokenToServer(String apiKey, String token) {
        Log.d("updateToken", "=" + apiKey);
        Log.d("updateToken", "=" + token);
        urlApi2.setUri(URLFCMTOKEN, apiKey);
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            RequestBody formBody = new FormBody.Builder()
                    .add("SerialNumber", apiKey + "")
                    .add("FCMtoken", token + "")
                    .add("sid", sid)
                    .build();
            Request request = new Request.Builder()
                    .url(urlApi2.getUrl())
                    .post(formBody)
                    .build();
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
