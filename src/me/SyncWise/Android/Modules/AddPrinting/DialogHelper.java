package me.SyncWise.Android.Modules.AddPrinting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

 

public class DialogHelper {
    public static final int ICE_CREAM_SANDWICH = 14;
    public static final int ICE_CREAM_SANDWICH_MR1 = 15;
    public static final int JELLY_BEAN = 16;
    public static final int JELLY_BEAN_MR1 = 17;
    public static final int JELLY_BEAN_MR2 = 18;
    public static final int KITKAT = 19;
    public static final int KITKAT_WATCH = 20;
    public static final int LOLLIPOP = 21;
    public static final int LOLLIPOP_MR1 = 22;
    public static final int M = 23;
    public static final int N = 24;
    public static final int N_MR1 = 25;
    public static final int O = 26;
    public static final int O_MR1 = 27;
    public static final int P = 28;
    public static final int Q = 29;
    public static final int RR = 30;
    public static final int S = 31;
    public static final int S_V2 = 32;
    public static void OneButtonDialog(Context context, String Title, String Message, final Runnable OkClicked) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >=  LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(Title)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkClicked.run();
                    }
                })
                .setMessage(Message)
               // .setIcon(R.drawable.warning_dark)
                .show()
                .setCancelable(false);
    }

    public static void TwoButtonsDialog(Context context, String Title, String Message, final Runnable CancelClicked , final Runnable OkClicked) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >=   LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OkClicked.run();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       CancelClicked.run();
                    }
                })
              //  .setIcon(R.drawable.warning_dark)
                .show()
                .setCancelable(false);;
    }

    public static void OneButtonDialog_2(Context context, String Title, String Message, final Runnable OkClicked, int icon) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >=  LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder
                .setTitle(Title)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkClicked.run();
                    }
                })
                .setIcon(icon)
               // .setMessage(Message)
                .show()
                .setCancelable(false);
    }
    public static void OneButtonDialog(Context context, String Title, String Message, final Runnable OkClicked, int icon) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >=  LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(Title)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkClicked.run();
                    }
                })
                .setMessage(Message)
                .setIcon(icon)
                .show()
                .setCancelable(false);
    }
    public static void TwoButtonsDialog(Context context, String Title, String Message, final Runnable CancelClicked , final Runnable OkClicked,int icon) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >=  LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OkClicked.run();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CancelClicked.run();
                    }
                })
                .setIcon(icon)
                .show()
                .setCancelable(false);;
    }
}
