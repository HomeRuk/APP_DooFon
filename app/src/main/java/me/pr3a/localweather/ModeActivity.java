package me.pr3a.localweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import me.pr3a.localweather.Helper.MyAlertDialog;
import me.pr3a.localweather.Helper.MyNetwork;
import me.pr3a.localweather.Helper.UrlApi;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final static String FILENAME = "Serialnumber.txt";
    private final static String url1 = "http://www.doofon.me/device/";
    private final static String url2 = "http://www.doofon.me/device/update/mode";
    private final UrlApi urlApi1 = new UrlApi();
    private final UrlApi urlApi2 = new UrlApi();
    private final static int READ_BLOCK_SIZE = 100;
    private final MyAlertDialog dialog = new MyAlertDialog();
    private int mode = 1;
    private String sid = "Ruk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        //Display Toolbar
        this.showToolbar("Setting Mode", "Mode Prediction");
        //Show DrawerLayout and drawerToggle
        this.initInstances();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
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
        // Connect loadJson choice 1 setTime
        this.conLoadJSON();
        Toast.makeText(this, "Refresh Mode Prediction", Toast.LENGTH_SHORT).show();
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
                Intent intentMain = new Intent(this, MainActivity.class);
                startActivity(intentMain);
                break;
            case R.id.nav_DeviceProfile:
                finish();
                overridePendingTransition(0, 0);
                Intent intentDevice = new Intent(this, DeviceActivity.class);
                startActivity(intentDevice);
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
                Intent intentSetting = new Intent(this, SettingsActivity.class);
                startActivity(intentSetting);
                break;
            case R.id.nav_mode:
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                break;
            case R.id.nav_disconnect:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(MyAlertDialog.titleDisconnect);
                dialog.setMessage(MyAlertDialog.messageDisconnect);
                dialog.setIcon(R.drawable.ic_clear_black_24dp);
                dialog.setCancelable(true);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FileOutputStream fOut = null;
                        try {
                            fOut = openFileOutput(FILENAME, MODE_PRIVATE);
                            OutputStreamWriter writer = new OutputStreamWriter(fOut);
                            writer.write("");
                            writer.flush();
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ModeActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
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

    //Button back
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
                FileOutputStream fOut = null;
                try {
                    fOut = openFileOutput(FILENAME, MODE_PRIVATE);
                    OutputStreamWriter writer = new OutputStreamWriter(fOut);
                    writer.write("");
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(ModeActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
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


    // Button Save threshold
    public void onButtonSave1(View view) {
        RadioButton radio1 = (RadioButton) findViewById(R.id.radioButton1);
        RadioButton radio2 = (RadioButton) findViewById(R.id.radioButton2);
        if (radio1.isChecked()) {
            mode = 1;
        } else if (radio2.isChecked()) {
            mode = 2;
        }

        //Check Connect network
        if (MyNetwork.isNetworkConnected(this)) {
            view.setEnabled(false);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Log.d("APP", "doInBackground");
                    try {
                        RequestBody formBody = new FormBody.Builder()
                                .add("SerialNumber", urlApi2.getApikey())
                                .add("mode", mode + "")
                                .add("sid", sid)
                                .build();
                        Request request = new Request.Builder()
                                .url(urlApi2.getUrl())
                                .post(formBody)
                                .build();
                        OkHttpClient okHttpClient = new OkHttpClient();
                        okHttpClient.newCall(request).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.showProblemDialog(ModeActivity.this, "Problem", "Save Fail");
                    }
                    return null;
                }
            }.execute();
            dialog.showConnectDialog(ModeActivity.this, "Save", "Success");
            view.setEnabled(true);
        } else dialog.showProblemDialog(this, "Problem", "Not Connected Network");

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
            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
            //readData Serialnumber
            this.readData();
            //LoadJSON
            new LoadJSON2().execute(urlApi1.getUri());
        } else {
            dialog.showProblemDialog(this, "Problem", "Not Connected Network");
        }
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }
    //Read SerialNumber
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
            if (data.equals("")) dialog.showProblemDialog(this, "Problem", "Data Empty");
            else {
                //Set url
                urlApi1.setUri(url1, data);
                urlApi2.setUri(url2, data);
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
        }
    }

    // AsyncTask Load Data Device
    private class LoadJSON2 extends AsyncTask<String, Void, String> {
        private final RadioButton radio1 = (RadioButton) findViewById(R.id.radioButton1);
        private final RadioButton radio2 = (RadioButton) findViewById(R.id.radioButton2);

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
            Log.d("APP", "onPostExecute");
            super.onPostExecute(result);
            try {
                JSONObject json = new JSONObject(result);
                String mode = String.format("%s", json.getString("mode"));
                //dialog.showProblemDialog(ModeActivity.this, "Problem", mode);

                if (mode.equals("1")) {
                    radio1.setChecked(true);
                    System.out.println(mode + ".....");
                } else if (mode.equals("2")) {
                    radio2.setChecked(true);
                    System.out.println(mode + "######");
                }
            } catch (Exception e) {
                dialog.showProblemDialog(ModeActivity.this, "Problem", "Not Connected Internet2");
                e.printStackTrace();
            }
        }
    }

}
