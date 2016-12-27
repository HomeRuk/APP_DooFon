package me.pr3a.localweather;

import android.content.Intent;
//import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import me.pr3a.localweather.Helper.MyNetwork;
import me.pr3a.localweather.Helper.UrlApi;
import me.pr3a.localweather.Helper.MyAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectDeviceActivity extends AppCompatActivity {

    private UrlApi urlApi = new UrlApi();
    private MyAlertDialog dialog = new MyAlertDialog();
    private String serial;
    private final static String url = "http://www.doofon.me/device/";
    private SharedPreferences mPreferences;
    private EditText editSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        bindWidgets();

        mPreferences =  getSharedPreferences("Serialnumber",MODE_PRIVATE);

        /*String Ccontent;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Ccontent = bundle.getString("C_content");
            editSerial.setText(Ccontent);
        }*/
    }

    private void bindWidgets() {
        editSerial = (EditText) findViewById(R.id.serial);
    }

    // Save Serial to SharedPreferences
    private void putPreference() {
        serial = editSerial.getText().toString();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("Serial", serial);
        editor.apply();
    }

    // Button Connect
    public void onButtonConnect(View view) {
        serial = editSerial.getText().toString();
        if (!serial.isEmpty()) {
            //Check Connect network
            if (MyNetwork.isNetworkConnected(this)) {
                //Set url & LoadJSON
                urlApi.setUri(url, serial);
                new LoadJSON1().execute(urlApi.getUri());
                this.putPreference();
            } else
                dialog.showProblemDialog(ConnectDeviceActivity.this, "Problem", "Not Connected Network");
        } else
            Toast.makeText(ConnectDeviceActivity.this, "Please fill in Serial Number", Toast.LENGTH_SHORT).show();
    }

    // Button Connect
    public void onButtonBarcode(View view) {
        /*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            finish();
            Intent intentBarcode = new Intent(this, ScannerActivity.class);
            startActivity(intentBarcode);
        }*/
        finish();
        overridePendingTransition(0, 0);
        Intent intentBarcode = new Intent(this, ScannerActivity.class);
        startActivity(intentBarcode);
    }

    // AsyncTask Load Data Device
    public class LoadJSON1 extends AsyncTask<String, Void, String> {

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
            Log.d("APP", "onPostExecute");
            super.onPostExecute(result);
            try {
                JSONObject json = new JSONObject(result);
                String Serial = String.format("%s", json.getString("SerialNumber"));
                if (Serial != null) {
                    finish();
                    overridePendingTransition(0, 0);
                    Intent intent = new Intent(ConnectDeviceActivity.this, MainActivity.class);
                    //intent.putExtra("Data_SerialNumber", Serial);
                    startActivity(intent);
                    Toast.makeText(ConnectDeviceActivity.this, "Connection successfully!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                //Clear SharedPreferences
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.remove("Serial");
                editor.apply();

                dialog.showConnectDialog(ConnectDeviceActivity.this, "Connect", "Connect UnSuccess");
                e.printStackTrace();
            }
        }
    }
}
