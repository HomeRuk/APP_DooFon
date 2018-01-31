package me.pr3a.localweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
//import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.pr3a.localweather.Helper.MyAlertDialog;
import me.pr3a.localweather.Helper.UrlApi;
import me.pr3a.localweather.Helper.MyNetwork;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.pr3a.localweather.Helper.MyNetwork.URLDEVICE;

public class ScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private UrlApi urlApi = new UrlApi();
    private MyAlertDialog dialog = new MyAlertDialog();
    private static String serial;
    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);
        setupToolbar();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

        mPreferences = getSharedPreferences("Serialnumber", MODE_PRIVATE);
    }

    // Activity onStart is Rule Start Service Location
    @Override
    protected void onStart() {
        Log.i("APP", "onStart");
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    // Save Serial to SharedPreferences
    private void putPreference(String serial) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("Serial", serial);
        editor.apply();
    }

    @Override
    public void handleResult(Result rawResult) {
        //Toast.makeText(this, "Contents = " + rawResult.getText() +
        //       ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        serial = rawResult.getText();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Confirm ");
        dialog.setMessage("Confirm Serialnumber: " + serial);
        dialog.setIcon(R.drawable.ic_done_blue24dp);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                connectDevice(serial);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

        // Note:
        // * Wait 5 seconds to resume the preview.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        }, 2000);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }*/

    // Button back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, 0);
        Intent intent = new Intent(this, ConnectDeviceActivity.class);
        startActivity(intent);
    }

    private void connectDevice(String serial) {
        if (serial != null) {
            if (!serial.isEmpty()) {
                //Check Connect network
                if (MyNetwork.isNetworkConnected(this)) {
                    //Set url & LoadJSON
                    urlApi.setUri(URLDEVICE, serial);
                    new LoadJsonDeviceScanner().execute(urlApi.getUri());
                    this.putPreference(serial);
                } else
                    dialog.showProblemDialog(this, "Problem", "Not Connected Network");
            } else
                Toast.makeText(this, "Please fill in Serial Number", Toast.LENGTH_SHORT).show();
        }
    }

    // AsyncTask Load Data Device
    private class LoadJsonDeviceScanner extends AsyncTask<String, Void, String> {

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
                if (Serial.equals(serial)) {
                    finish();
                    overridePendingTransition(0, 0);
                    Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(ScannerActivity.this, "Connection successfully!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //Clear SharedPreferences
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.remove("Serial");
                editor.apply();

                dialog.showConnectDialog(ScannerActivity.this, "Connect", "Connection failed");
                e.printStackTrace();
            }
        }
    }
}
