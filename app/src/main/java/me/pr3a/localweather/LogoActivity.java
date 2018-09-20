package me.pr3a.localweather;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import me.pr3a.localweather.Helper.MyAlertDialog;
import me.pr3a.localweather.Helper.MyNetwork;
import me.pr3a.localweather.Helper.UrlApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.pr3a.localweather.Helper.MyNetwork.URLDEVICE;

public class LogoActivity extends AppCompatActivity {

    private final UrlApi urlApi = new UrlApi();
    private final MyAlertDialog dialog = new MyAlertDialog();
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        //set fond
        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weather.ttf");
        TextView weatherIcon = (TextView) findViewById(R.id.logo);
        weatherIcon.setTypeface(weatherFont);
        weatherIcon.setText(getString(R.string.weather_rain));

        if (MyNetwork.isNetworkConnected(this)) {
            //Read SerialNumber and if has SerialNumber connect device
            mPreferences = getSharedPreferences("Serialnumber", MODE_PRIVATE);
            getPreference();
        } else {
            dialog.showProblemDialog(LogoActivity.this, "Problem", "Not Connected Network");
        }
    }


    private void getPreference() {
        try {
            if (mPreferences.contains("Serial")) {
                String serial = mPreferences.getString("Serial", "");
                //Set url & LoadJSON
                urlApi.setUri(URLDEVICE, serial);
                new LoadJSON0(LogoActivity.this).execute(urlApi.getUri());
            } else {
                intentDelay();
            }
        } catch (Exception e) {
            //Clear SharedPreferences
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.remove("Serial");
            editor.apply();

            //Link to Page ConnectDevice
            intentDelay();

            e.printStackTrace();
        }
    }

    // Delay to page ConnectDevice
    private void intentDelay() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LogoActivity.this, ConnectDeviceActivity.class));
                finish();
            }
        }, 1500);
    }

    // AsyncTask Load Data Device
    @SuppressLint("StaticFieldLeak")
    private class LoadJSON0 extends AsyncTask<String, Void, String> {
        // ProgressDialog
        private final ProgressDialog mProgressDialog;

        LoadJSON0(LogoActivity activity) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d("APP", "doInBackground");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(urls[0]).build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Not Success - code : " + response.code();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("APP", "onPostExecute");
            try {
                JSONObject json = new JSONObject(result);
                String Serial = String.format("%s", json.getString("SerialNumber"));
                if (Serial != null) {
                    Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    dialog.showConnectDialog(LogoActivity.this, "Connect", "Connection failed");
                }
            } catch (Exception e) {
                //Clear SharedPreferences
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.remove("Serial");
                editor.apply();

                dialog.showProblemDialog(LogoActivity.this, "Problem", "Program Stop");
            }
            mProgressDialog.dismiss();
        }
    }
}
