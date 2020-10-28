package app.savetalk.upload;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.savetalk.db.ElementDataSource;
import app.savetalk.db.QueueElement;
import app.savetalk.util.SettingReader;

public class UploadService extends Service {
    private Context context = this;
    private List<QueueElement> elementos;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Activamos foreground
        UploadNotifications notis = new UploadNotifications();
        startForeground(UploadNotifications.GENERAL_NOTIFICATION_ID, notis.getNotificacionServicioActivo(context));

        //Salimos si la pantalla está bloqueada, no podemos subir con ella así
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isInteractive()) {
            this.stopSelf();
            return;
        }

        //Salimos si esta conectado por datos y no se permite la subida por datos
        if(SettingReader.Uploads.subidaSoloWifi(this) && !isConnectedOverWifi()) {
            this.stopSelf();
            return;
        }

        if(SettingReader.General.mostrarInfoDebug(context))
            Toast.makeText(this, "Iniciado uploader", Toast.LENGTH_LONG).show();
        cargarElementos();

        if(SettingReader.Uploads.isFtpUploadEnabled(this))
            subirFTP();

        this.stopSelf();
    }

    private void cargarElementos() {
        ElementDataSource ds = new ElementDataSource(context);
        ds.open();
        elementos = ds.getAllQueueElements();
        ds.close();
    }

    private void subirFTP() {
        List<QueueElement> filePaths = new ArrayList<QueueElement>();
        for(QueueElement elem : elementos){
            if(!elem.isFTP_UPLOADED()){
                //El elemento no se ha subido por FTP, debemos comprobar si aun no se ha borrado
                File file = new File(elem.getFilePath());
                if(file.exists()){
                        filePaths.add(elem);
                }else{
                    //Retiramos el elemento de la bbdd si no existe
                    deleteFromQueue(elem);
                }
            }
        }
        if(filePaths.size() == 0)
            return;
        FTPUploader ftpUploader = new FTPUploader(context, filePaths.toArray(new QueueElement[filePaths.size()]));
        Thread hilo = new Thread(ftpUploader);
        hilo.start();
    }

    private boolean isConnectedOverWifi() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    private void deleteFromQueue(QueueElement elem) {
        ElementDataSource ds = new ElementDataSource(context);
        ds.open();
        ds.removeElement(elem.getID());
        ds.close();
    }

    @Override
    public void onDestroy() {
        if(SettingReader.General.mostrarInfoDebug(context))
            Toast.makeText(this, "Detenido uploader", Toast.LENGTH_LONG).show();
    }
}