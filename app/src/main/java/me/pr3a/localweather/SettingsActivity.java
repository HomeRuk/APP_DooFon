package me.pr3a.localweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import at.grabner.circleprogress.CircleProgressView;
import me.pr3a.localweather.Helper.MyAlertDialog;
import me.pr3a.localweather.Helper.MyNetwork;
import me.pr3a.localweather.Helper.UrlApi;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private final static String url1 = "http://192.168.44.51/DooFon/public/api/device/";
    private final static String url2 = "http://192.168.44.51/DooFon/public/api/device/update/threshold";
    private final UrlApi urlApi1 = new UrlApi();
    private final UrlApi urlApi2 = new UrlApi();
    private final MyAlertDialog dialog = new MyAlertDialog();
    private SharedPreferences mPreferences;
    private TextView txtValue;
    private CircleProgressView mCircleView;
    private int valueInt = 0;
    private final String sid = "Ruk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Display Toolbar
        this.showToolbar("Setting", "Predict Threshold");
        //Show DrawerLayout and drawerToggle
        this.initInstances();

        txtValue = (TextView) findViewById(R.id.textView_seekBar);

        //Read Serialnumber & setUrl
        mPreferences = getSharedPreferences("Serialnumber", MODE_PRIVATE);
        conLoadJSON();

        this.onCircleView();
    }

    // Create MenuBar on Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings_toolbar, menu);
        return true;
    }

    // Click button refresh
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting_refresh) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Refresh Threshold");
            dialog.setMessage("Do you want to Minimum Threshold refresh ?");
            dialog.setIcon(R.drawable.ic_loop_black_24dp);
            dialog.setCancelable(true);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    conLoadJSON();
                    Toast.makeText(SettingsActivity.this, "Refresh Setting Threshold", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
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
                startActivity(getIntent());
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
                        MyCustomFirebaseInstanceIdService.sendTokenToServer(urlApi1.getApikey(), "0");
                        //Clear SharedPreferences
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Toast.makeText(SettingsActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
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
                //Clear FCMtoken in DB
                MyCustomFirebaseInstanceIdService.sendTokenToServer(urlApi1.getApikey(), "0");
                //Clear SharedPreferences
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(SettingsActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
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

    // Button Save threshold
    public void onButtonSave(final View view) {

        AlertDialog.Builder dialogSave = new AlertDialog.Builder(this);
        dialogSave.setTitle("Save Threshold");
        dialogSave.setMessage("Do you want to Save Minimum Threshold ?");
        dialogSave.setIcon(R.drawable.ic_done_blue24dp);
        dialogSave.setCancelable(true);
        dialogSave.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogSave, int which) {
                //Check serial is not empty
                if (valueInt != 0) {
                    //Check Connect network
                    if (MyNetwork.isNetworkConnected(SettingsActivity.this)) {
                        view.setEnabled(false);
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                Log.d("APP", "doInBackground");
                                try {
                                    RequestBody formBody = new FormBody.Builder()
                                            .add("SerialNumber", urlApi2.getApikey())
                                            .add("threshold", valueInt + "")
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
                                    dialog.showProblemDialog(SettingsActivity.this, "Problem", "Save Fail");
                                }
                                return null;
                            }
                        }.execute();
                        //dialog.showConnectDialog(SettingsActivity.this, "Save", "Success");
                        view.setEnabled(true);
                    } else {
                        dialog.showProblemDialog(SettingsActivity.this, "Problem", "Not Connected Network");
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Please Select threshold", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogSave.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    // mCircleView
    private void onCircleView() {
        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                valueInt = Math.round(value);
                txtValue.setText("Threshold : " + valueInt + " %");
                //seekBar.setProgress(valueInt);
            }
        });
    }

    /*
        // SeekBar
        private void onSeekBar() {
            seekBar = (SeekBar) findViewById(R.id.seek_Bar);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged = progress;
                    txtSeekBar.setText("Threshold : " + progressChanged + "%");
                    mCircleView.setValueAnimated(progressChanged, 1500);
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    mCircleView.setValueAnimated(seekBar.getProgress(), 15);
                    Toast.makeText(SettingsActivity.this, "Threshold : " + progressChanged + "%", Toast.LENGTH_SHORT).show();
                }
            });
        }
    */
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
            new LoadJSON2().execute(urlApi1.getUri());
        } else {
            dialog.showProblemDialog(this, "Problem", "Not Connected Network");
        }
    }

    //Read SerialNumber
    private void getPreference() {
        try {
            if (mPreferences.contains("Serial")) {
                String serial = mPreferences.getString("Serial", "");
                //Set url & LoadJSON
                urlApi1.setUri(url1, serial);
                urlApi2.setUri(url2, serial);
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
    private class LoadJSON2 extends AsyncTask<String, Void, String> {

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
                String threshold = String.format("%s", json.getString("threshold"));
                txtValue.setText(String.format("Threshold : %s %%", threshold));
                //seekBar.setProgress(Integer.parseInt(threshold));
                mCircleView.setValueAnimated(Integer.parseInt(threshold), 1500);
            } catch (Exception e) {
                dialog.showProblemDialog(SettingsActivity.this, "Problem", "Not Connected Internet");
                e.printStackTrace();
            }
        }
    }
}
