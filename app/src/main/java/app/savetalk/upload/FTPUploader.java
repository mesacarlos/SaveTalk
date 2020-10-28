package app.savetalk.upload;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.File;
import java.io.FileInputStream;

import app.savetalk.db.ElementDataSource;
import app.savetalk.db.QueueElement;
import app.savetalk.util.SettingReader;

public class FTPUploader implements Runnable{
    private Context context;
    private QueueElement[] filePaths;

    private int totalSucess = 0;
    private int processedSoFar = 0;

    public FTPUploader(Context context, QueueElement[] filePaths){
        this.context = context;
        this.filePaths = filePaths;
    }

    @Override
    public void run() {
        UploadNotifications notis = new UploadNotifications();
        try {
            FTPClient con;
            int tipoCliente = SettingReader.Uploads.getTipoClienteFtp(context);
            switch (tipoCliente){
                case 0:
                    con = new FTPClient();
                    break;
                case 1:
                    con = new FTPSClient();
                    break;
                case 2:
                    con = new FTPSClient(true);
                    break;
                default:
                    Log.v("Resultado de subida FTP", "Cliente FTP no especificado");
                    if(SettingReader.Uploads.mostrarNotis(context))
                        notis.mostrarErrorFTP(context, "Especifique la configuración TLS/SSL en configuración para subir vía FTP.");
                    return;
            }
            con.connect(SettingReader.Uploads.getFtpIp(context), SettingReader.Uploads.getFtpPort(context));

            if (con.login(SettingReader.Uploads.getFtpUsername(context), SettingReader.Uploads.getFtpPassword(context))) {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                if(!con.changeWorkingDirectory(SettingReader.Uploads.getFtpPath(context))){
                    //No existe la carpeta que el usuario estableció por defecto
                    con.logout();
                    con.disconnect();
                    Log.v("Resultado de subida FTP", "Error, no existe la carpeta FTP indicada por el usuario");
                    if(SettingReader.Uploads.mostrarNotis(context))
                        notis.mostrarErrorFTP(context, "No existe la carpeta indicada en el FTP remoto.");
                    return;
                }
                for(QueueElement elem : filePaths){
                    processedSoFar++;
                    File fichero = new File(elem.getFilePath());
                    FileInputStream in = new FileInputStream(fichero);
                    boolean result = con.storeFile(fichero.getName(), in);
                    in.close();

                    if (result){
                        Log.v("Resultado de subida FTP", "Subida correcta");
                        totalSucess++;
                        marcarComoSubido(elem);
                        if(SettingReader.Uploads.mostrarNotis(context))
                            notis.mostrarEstadoFTP(context, totalSucess, processedSoFar-totalSucess);
                        if(SettingReader.Uploads.borrarAlSubirFTP(context))
                            fichero.delete();
                    }
                }
                con.logout();
                con.disconnect();
            }else{
                Log.v("Resultado de subida FTP", "Error al logear");
                notis.mostrarErrorFTP(context, "Credenciales FTP incorrectas.");
            }
        } catch (Exception e) {
            Log.v("Resultado de subida FTP", "Error al subir");
            if(SettingReader.Uploads.mostrarNotis(context)){
                if(SettingReader.General.mostrarInfoDebug(context))
                    notis.mostrarErrorFTP(context, "Error al subir vía FTP: " + e.getMessage());
                else
                    notis.mostrarErrorFTP(context, "Error al procesar la cola de subidas vía FTP.");
            }

            e.printStackTrace();
        }
    }

    private void marcarComoSubido(QueueElement elem){
        ElementDataSource ds = new ElementDataSource(context);
        ds.open();
        ds.marcarElementoSubidoFTP(elem.getID(), true);
        ds.close();
    }
}