package me.pr3a.localweather.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import me.pr3a.localweather.R;

public class MyAlertDialog {

    public static String titleDisconnect = "Sign Out";
    public static String messageDisconnect = "Do you want to Sign Out ?";

    /**
     * ShowAlertConnectDialog
     *
     * @param context
     * @param title
     * @param message
     */
    public void showConnectDialog(Context context, String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    /**
     * ShowAlertProblemDialog
     *
     * @param activity
     * @param title
     * @param message
     */
    public void showProblemDialog(final Activity activity, String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //activity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }).show();
    }

    /*
    public void confirmConnectDialog(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_home_black_24dp);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
    */
}
