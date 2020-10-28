package app.savetalk.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import app.savetalk.R;

public class Dialogos {
    public static void showInfoDialog(Context context, String info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Información")
                .setMessage(info)
                .setIcon(R.drawable.ic_info_black_24dp)
                .show();
    }
}
