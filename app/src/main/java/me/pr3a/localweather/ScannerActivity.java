package me.pr3a.localweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
//import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.pr3a.localweather.Helper.MyAlertDialog;
import me.pr3a.localweather.Helper.UrlApi;
import me.pr3a.localweather.Helper.MyNetwork;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private UrlApi urlApi = new UrlApi();
    private MyAlertDialog dialog = new MyAlertDialog();
    private final static String url = "http://www.doofon.me/device/";
    private final static String FILENAME = "Serialnumber.txt";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);
        setupToolbar();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
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

    @Override
    public void handleResult(Result rawResult) {
        //Toast.makeText(this, "Contents = " + rawResult.getText() +
        //       ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        final String serial = rawResult.getText();

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
                    urlApi.setUri(url, serial);
                    new LoadJSONDevice().execute(urlApi.getUri());
                } else
                    dialog.showProblemDialog(this, "Problem", "Not Connected Network");
            } else
                Toast.makeText(this, "Please fill in Serial Number", Toast.LENGTH_SHORT).show();
        }
    }

    // AsyncTask Load Data Device
    public class LoadJSONDevice extends AsyncTask<String, Void, String> {

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
                    try {
                        //Writer Data Serial
                        FileOutputStream fOut = openFileOutput(FILENAME, MODE_PRIVATE);
                        OutputStreamWriter writer = new OutputStreamWriter(fOut);
                        writer.write(Serial);
                        writer.flush();
                        writer.close();
                    } catch (IOException ioe) {
                        dialog.showConnectDialog(ScannerActivity.this, "Connect", "Writer Data fail");
                        ioe.printStackTrace();
                    }
                    Toast.makeText(ScannerActivity.this, "Save successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                    Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
                    //intent.putExtra("Data_SerialNumber", Serial);
                    startActivity(intent);
                }
            } catch (Exception e) {
                try {
                    //Writer Data Serial
                    FileOutputStream fOut = openFileOutput(FILENAME, MODE_PRIVATE);
                    OutputStreamWriter writer = new OutputStreamWriter(fOut);
                    writer.write("");
                    writer.flush();
                    writer.close();
                } catch (IOException ioe) {
                    dialog.showConnectDialog(ScannerActivity.this, "Connect", "Writer Data fail");
                    ioe.printStackTrace();
                }
                dialog.showConnectDialog(ScannerActivity.this, "Connect", "Connect UnSuccess");
                e.printStackTrace();
            }
        }
    }
}
