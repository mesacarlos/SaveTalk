package app.savetalk.upload;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import app.savetalk.R;
import app.savetalk.util.Notifications;

public class UploadNotifications {
    public static final int GENERAL_NOTIFICATION_ID = 99;
    public static final int FTP_NOTIFICATION_ID = 100;
    public static final int FTP_NOTIFICATION_ID_ERR = 101;

    public Notification getNotificacionServicioActivo(Context context){
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notifications.createNotificationChannel(context);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
                .setContentTitle("Subiendo archivos")
                .setContentText("El dispositivo está subiendo tus ficheros")
                .setSmallIcon(R.drawable.ic_audiotrack_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOnlyAlertOnce(true);
        return mBuilder.build();
    }

    public void mostrarEstadoFTP(Context context, int correctas, int fallidas){
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notifications.createNotificationChannel(context);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
                .setContentTitle("Subidas FTP")
                .setContentText(correctas + " transferencias correctas, " + fallidas + " fallidas.")
                .setSmallIcon(R.drawable.ic_audiotrack_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true);
        notificationManager.notify(FTP_NOTIFICATION_ID, mBuilder.build());
    }

    public void mostrarErrorFTP(Context context, String error){
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notifications.createNotificationChannel(context);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
                .setContentTitle("Error al subir vía FTP")
                .setContentText(error)
                .setSmallIcon(R.drawable.ic_audiotrack_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true);
        notificationManager.notify(FTP_NOTIFICATION_ID_ERR, mBuilder.build());
    }

    public void borrarNotificacion(Context context, int idNotificacion){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(idNotificacion);
    }
}