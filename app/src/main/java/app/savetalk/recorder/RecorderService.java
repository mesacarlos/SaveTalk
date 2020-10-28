package app.savetalk.recorder;

import android.app.Notification;
import android.app.Service;
import android.content.*;
import android.media.MediaRecorder;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import app.savetalk.R;
import app.savetalk.db.ElementDataSource;
import app.savetalk.db.QueueElement;
import app.savetalk.util.Notifications;
import app.savetalk.util.SettingReader;

public class RecorderService extends Service {
    private Timer timer;
    public Context context = this;
    public MediaRecorder mRecorder = null;
    private String ruta;

    /**
     * Not implemented yet.
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates recorder.
     */
    @Override
    public void onCreate() {
        //Creamos notificacion y lanzamos en foreground
        startForeground(288, crearNotificacion(context));

        //Mostramos info debug
        if(SettingReader.General.mostrarInfoDebug(context))
            Toast.makeText(this, getString(R.string.toast_started_rec), Toast.LENGTH_LONG).show();

        //Leemos ajustes
        int bitrate = SettingReader.AudioQuality.bitrate(this);
        ruta = SettingReader.General.rutaGuardado(this);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(ruta);
        mRecorder.setAudioEncodingBitRate(bitrate);
        mRecorder.setAudioSamplingRate(44100);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();
    }

    /**
     * Stops recorder, adds recorging to upload queue and deletes notification.
     */
    @Override
    public void onDestroy() {
        //Añadimos elemento a la cola para subirlo
        ElementDataSource ds = new ElementDataSource(context);
        QueueElement elem = new QueueElement(0, ruta, false, false, false);
        ds.open();
        ds.insertQueueElement(elem);
        ds.close();

        //Paramos de grabar
        mRecorder.stop();
        //Retiramos notificacion
        timer.cancel();
        timer.purge();
        borrarNotificacion(context);
        //Informamos
        if(SettingReader.General.mostrarInfoDebug(context))
            Toast.makeText(this, getString(R.string.toast_stopped_rec), Toast.LENGTH_LONG).show();
    }

    /**
     * Creates recording notification. Please note this notification will update every second.
     * @param context Context
     * @return Notification already built.
     */
    public Notification crearNotificacion(final Context context){
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notifications.createNotificationChannel(context);

        final long instante = System.currentTimeMillis();
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
                .setContentTitle("Grabando ahora")
                .setContentText("Grabación de audio en curso")
                .setSmallIcon(R.drawable.ic_audiotrack_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOnlyAlertOnce(true);
        notificationManager.notify(288, mBuilder.build());

        //Creamos el actualizador automatico
        if(timer == null){
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    long diferencia = System.currentTimeMillis()-instante;
                    long minutos = TimeUnit.MILLISECONDS.toMinutes(diferencia);
                    long segundos = TimeUnit.MILLISECONDS.toSeconds(diferencia) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diferencia));
                    mBuilder.setContentText("[" + anadirCeroIzquierda(minutos) + ":" + anadirCeroIzquierda(segundos) + "] Grabación en curso");
                    notificationManager.notify(288, mBuilder.build());
                }
            };
            if(SettingReader.General.mostrarNotificacion(context))
                timer.scheduleAtFixedRate(task, 0, 500);
        }

        return mBuilder.build();
    }

    /**
     * Integer beautifier.
     * @param valor Value to beautify
     * @return String containing beautified number
     */
    public String anadirCeroIzquierda(long valor){
        if(valor<10)
            return "0" + valor;
        else
            return "" + valor;
    }

    /**
     * Deletes notification.
     * @param context Contexto
     */
    public void borrarNotificacion(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(288);
    }
}