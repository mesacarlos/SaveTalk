package app.savetalk.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.savetalk.util.SettingReader;

/**
 * Receives boot broadcast from system.
 * Starts RecorderService if user enabled "Start on boot" at settings.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(SettingReader.General.grabarEnInicio(context)){
            ManagerRecorder.getInstance().iniciarGrabacion(context);
        }
    }
}