package me.pr3a.localweather;

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

import me.pr3a.localweather.Helper.MyAlertDialog;
import me.pr3a.localweather.Helper.MyNetwork;
import me.pr3a.localweather.Helper.UrlApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogoActivity extends AppCompatActivity {

    private final UrlApi urlApi = new UrlApi();
    private final MyAlertDialog dialog = new MyAlertDialog();
    private final static String url = "http://192.168.44.51/DooFon/public/api/device/";
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
                urlApi.setUri(url, serial);
                new LoadJSON0().execute(urlApi.getUri());
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

    /* // Read SerialNumber
     private void readData() {
         try {
             FileInputStream fIn = openFileInput(FILENAME);
             InputStreamReader reader = new InputStreamReader(fIn);

             char[] buffer = new char[READ_BLOCK_SIZE];
             String data = "";
             int charReadCount;
             while ((charReadCount = reader.read(buffer)) > 0) {
                 String readString = String.copyValueOf(buffer, 0, charReadCount);
                 data += readString;
                 buffer = new char[READ_BLOCK_SIZE];
             }
             reader.close();
             if (!(data.equals(""))) {
                 //Set url & LoadJSON
                 urlApi.setUri(url, data);
                 new LoadJSON0().execute(urlApi.getUri());
             } else {
                 intentDelay();
             }
         } catch (Exception e) {
             e.printStackTrace();
             try {
                 FileOutputStream fOut = openFileOutput(FILENAME, MODE_PRIVATE);
                 OutputStreamWriter writer = new OutputStreamWriter(fOut);
                 writer.write("");
                 writer.flush();
                 writer.close();
             } catch (IOException ioe) {
                 ioe.printStackTrace();
             }
             intentDelay();
         }
     }
 */
    //Delay to page ConnectDevice
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
    private class LoadJSON0 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            Log.d("APP", "doInBackground");
            OkHttpClient okHttpClient = new OkHttpClient();
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
        }
    }
}
