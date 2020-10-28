package app.savetalk.recorder;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import app.savetalk.upload.UploadService;
import app.savetalk.util.SettingReader;

/**
 * Singleton class implementing a recorder manager.
 * Starting/Stopping recording MUST be done using this class.
 */
public class ManagerRecorder {
    private static ManagerRecorder singleton = null;
    private Timer timer;

    private ManagerRecorder(){}

    public static ManagerRecorder getInstance() {
        if (singleton == null)
            singleton = new ManagerRecorder();
        return singleton;
    }

    /**
     * Starts recording. If "Split recordings" is enabled within settings, manager
     * will stop and restart recording until user explicitly stops service.
     * @param context Context
     */
    public void iniciarGrabacion(Context context){
        if(SettingReader.General.trocearGrabaciones(context)){
            //Arrancamos indicando que debe trocear
            int minutos = SettingReader.General.duracionTroceado(context);
            _inicioTroceando(context, minutos);
        }else{
            //Arrancamos sin más
            _iniciar(context);
        }
    }

    /**
     * Starts recorging and a timer used to split them.
     * @param context Context
     * @param minutes Length of every recording in minutes.
     */
    private void _inicioTroceando(final Context context, final int minutes) {
        _iniciar(context);
        //Si el timer existe lo paramos
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
        //Ahora que si existia lo hemos parado, creamos uno nuevo
        timer = new Timer();
        //Y ponemos el timertask de nuevo
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Si no se esta ejecutando el daemon porque el user lo ha parado, no seguimos
                if(daemonEnEjecucion(context)){
                    _detener(context);
                    _inicioTroceando(context, minutes);
                }
            }
        }, TimeUnit.MINUTES.toMillis(minutes));
    }

    /**
     * Stops recording service.
     * @param context Context
     */
    public void pararGrabacion(Context context){
        //Si el timer existe lo paramos
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
        _detener(context);
    }

    /**
     * Just starts service
     * @param context Context
     */
    private void _iniciar(Context context) {
        context.startService(new Intent(context, RecorderService.class));
    }

    /**
     * Just stops service
     * @param context Context
     */
    private void _detener(Context context) {
        context.stopService(new Intent(context, RecorderService.class));

        //Arrancamos uploader
        context.startService(new Intent(context, UploadService.class));
    }

    /**
     * Checks if recording service is running in background.
     * @param context Contexto
     * @return true if daemon is running, false if not.
     */
    public static boolean daemonEnEjecucion(Context context) {
        Class<?> serviceClass = RecorderService.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}