package me.pr3a.localweather;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import me.pr3a.localweather.Helper.MyNetwork;
import me.pr3a.localweather.Helper.UrlApi;
import me.pr3a.localweather.Helper.MyAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.pr3a.localweather.Helper.MyNetwork.URLDEVICE;

public class DeviceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final UrlApi urlApi = new UrlApi();
    private final MyAlertDialog dialog = new MyAlertDialog();
    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        //Display Toolbar
        this.showToolbar("My Device", "");
        //Show DrawerLayout and drawerToggle
        this.initInstances();

         /*
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // GET sharedPreference Serialnumber on activity create
                                        mPreferences = getSharedPreferences("Serialnumber", MODE_PRIVATE);
                                        // LoadJSON data device
                                        conLoadJSON();
                                    }
                                }
        );
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        this.conLoadJSON();
        Toast.makeText(this, "Refresh Detail Device", Toast.LENGTH_SHORT).show();
    }

    // Select Menu Navigation
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_main:
                finish();
                overridePendingTransition(0, 0);
                Intent intentDevice = new Intent(this, MainActivity.class);
                startActivity(intentDevice);
                break;
            case R.id.nav_DeviceProfile:
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                break;
            case R.id.nav_location:
                finish();
                overridePendingTransition(0, 0);
                Intent intentLocation = new Intent(this, LocationActivity.class);
                startActivity(intentLocation);
                break;
            case R.id.nav_setting:
                finish();
                overridePendingTransition(0, 0);
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            /*
            case R.id.nav_mode:
                finish();
                overridePendingTransition(0, 0);
                Intent intentMode = new Intent(this, ModeActivity.class);
                startActivity(intentMode);
                break;
                */
            case R.id.nav_disconnect:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(MyAlertDialog.titleDisconnect);
                dialog.setMessage(MyAlertDialog.messageDisconnect);
                dialog.setIcon(R.drawable.ic_clear_black_24dp);
                dialog.setCancelable(true);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Clear FCMtoken in DB
                        MyCustomFirebaseInstanceIdService.sendTokenToServer(urlApi.getApikey(), "0");
                        //Clear SharedPreferences
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Toast.makeText(DeviceActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
                        //Restart APP
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Button back
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else {
            finish();
            overridePendingTransition(0, 0);
            Intent intentDevice = new Intent(this, MainActivity.class);
            startActivity(intentDevice);
            super.onBackPressed();
        }
    }

    // Button Disconnect
    public void onClickDisconnect(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(MyAlertDialog.titleDisconnect);
        dialog.setMessage(MyAlertDialog.messageDisconnect);
        dialog.setIcon(R.drawable.ic_clear_black_24dp);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Clear FCMtoken in DB
                MyCustomFirebaseInstanceIdService.sendTokenToServer(urlApi.getApikey(), "0");
                //Clear SharedPreferences
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.clear();
                editor.apply();
                //Show Toast massage
                Toast.makeText(DeviceActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();

                //Restart APP
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    // Show Toolbar
    private void showToolbar(String title, String subTitle) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setSubtitle(subTitle);
        setSupportActionBar(toolbar);
    }

    // Show DrawerLayout and drawerToggle
    private void initInstances() {
        // NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    // Connect Load Json
    private void conLoadJSON() {
        // Check Network Connected
        if (MyNetwork.isNetworkConnected(this)) {
            //readData Serialnumber
            this.getPreference();
            //LoadJSON
            new LoadJSON2(DeviceActivity.this).execute(urlApi.getUri());
        } else {
            dialog.showProblemDialog(this, "Problem", "Not Connected Network");
        }
    }

    // Read SerialNumber
    private void getPreference() {
        try {
            if (mPreferences.contains("Serial")) {
                String serial = mPreferences.getString("Serial", "");
                //Set url & LoadJSON
                urlApi.setUri(URLDEVICE, serial);
            }
        } catch (Exception e) {
            //Clear SharedPreferences
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.remove("Serial");
            editor.apply();
            e.printStackTrace();
        }
    }

    // AsyncTask Load Data Device
    @SuppressLint("StaticFieldLeak")
    private class LoadJSON2 extends AsyncTask<String, Void, String> {

        private final TextView deviceSerialNumber = (TextView) findViewById(R.id.txt_SerialNumber);
        private final TextView deviceLatitude = (TextView) findViewById(R.id.txt_Latitude);
        private final TextView deviceLongitude = (TextView) findViewById(R.id.txt_Longitude);
        private final TextView deviceThreshold = (TextView) findViewById(R.id.txt_Threshold);
        private final TextView deviceCreate = (TextView) findViewById(R.id.txt_Create);
        private final TextView deviceUpdated = (TextView) findViewById(R.id.txt_Updated);
        // ProgressDialog
        private final ProgressDialog mProgressDialog;

        LoadJSON2(DeviceActivity activity) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
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
            super.onPostExecute(result);

            //Read Json and Show DetailDevice
            try {
                JSONObject json = new JSONObject(result);
                //System.out.println(result);
                String Serial = String.format("%s", json.getString("SerialNumber"));
                String latitude = String.format("%s", json.getString("latitude"));
                String longitude = String.format("%s", json.getString("longitude"));
                String threshold = String.format("%s", json.getString("threshold"));
                String created_at = String.format("%s", json.getString("created_at"));
                String updated_at = String.format("%s", json.getString("updated_at"));

                deviceSerialNumber.setText(String.format("%s", Serial));
                deviceLatitude.setText(String.format("%s", latitude));
                deviceLongitude.setText(String.format("%s", longitude));
                deviceThreshold.setText(String.format("%s", threshold));
                deviceCreate.setText(String.format("%s", created_at));
                deviceUpdated.setText(String.format("%s", updated_at));
            } catch (Exception e) {
                dialog.showProblemDialog(DeviceActivity.this, "Problem", "Read Data fail");
                e.printStackTrace();
            }
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);
            mProgressDialog.dismiss();

            // * Wait 0.5 seconds to close progressdialog
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            }, 500);
        }
    }

}
