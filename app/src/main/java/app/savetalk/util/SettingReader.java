package app.savetalk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.savetalk.ui.SettingsActivity;

public class SettingReader {
    public static class General{
        public static boolean grabarEnInicio(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.GeneralPreferenceFragment.KEY_BOOL_GRABAR_INICIO, false);
        }

        public static String rutaGuardado(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String ruta = sharedPref.getString(SettingsActivity.GeneralPreferenceFragment.KEY_RUTA_GUARDADO, "{storage}/SaveTalk/REC-{datetime}.aac")
                    .replace("{storage}", Environment.getExternalStorageDirectory().getAbsolutePath())
                    .replace("{timespan}", System.currentTimeMillis() + "")
                    .replace("{datetime}", dateFormat.format(date));
            new File(ruta).getParentFile().mkdirs(); //Creamos subcarpetas si no existen
            return ruta;
        }

        public static boolean mostrarNotificacion(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.GeneralPreferenceFragment.KEY_SHOW_NOTIFICATION, false);
        }

        public static boolean mostrarInfoDebug(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.GeneralPreferenceFragment.KEY_BOOL_SHOW_DEBUG_TOAST, false);
        }

        public static boolean trocearGrabaciones(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.GeneralPreferenceFragment.KEY_BOOL_CORTAR_GRAB, false);
        }

        public static int duracionTroceado(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String valor = sharedPref.getString(SettingsActivity.GeneralPreferenceFragment.KEY_TIEMPO_SPLIT, "30");
            return (int)Float.parseFloat(valor);
        }
    }

    public static class AudioQuality{
        public static int bitrate(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String valor = sharedPref.getString(SettingsActivity.AudioQualityPreferenceFragment.KEY_BITRATE, "320000");
            return (int)Float.parseFloat(valor);
        }

    }

    public static class Uploads{
        public static boolean subidaSoloWifi(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.DataSyncPreferenceFragment.KEY_BOOL_WIFI_ONLY, true);
        }

        public static boolean mostrarNotis(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.DataSyncPreferenceFragment.KEY_BOOL_NOTIS_ENABLED, true);
        }

        public static boolean isFtpUploadEnabled(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.DataSyncPreferenceFragment.KEY_BOOL_FTP_ENABLED, false);
        }

        public static boolean borrarAlSubirFTP(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean(SettingsActivity.DataSyncPreferenceFragment.KEY_BOOL_DELETE_AFTER_FTP_UPLOAD, false);
        }

        public static String getFtpIp(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String ip = sharedPref.getString(SettingsActivity.DataSyncPreferenceFragment.KEY_IP_FTP, "192.168.1.30");
            return ip;
        }

        public static int getFtpPort(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String puerto = sharedPref.getString(SettingsActivity.DataSyncPreferenceFragment.KEY_PORT_FTP, "21");
            return Integer.parseInt(puerto);
        }

        public static int getTipoClienteFtp(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String tipo = sharedPref.getString(SettingsActivity.DataSyncPreferenceFragment.KEY_CLIENTTYPE_FTP, "0");
            return Integer.parseInt(tipo);
        }

        public static String getFtpUsername(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String user = sharedPref.getString(SettingsActivity.DataSyncPreferenceFragment.KEY_USER_FTP, "Anonimo");
            return user;
        }

        public static String getFtpPassword(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String passwd = sharedPref.getString(SettingsActivity.DataSyncPreferenceFragment.KEY_PASSWD_FTP, "neverUse1234");
            return passwd;
        }

        public static String getFtpPath(Context context){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String defpath = sharedPref.getString(SettingsActivity.DataSyncPreferenceFragment.KEY_DEFPATH_FTP, "/Audios/SaveTalk/");
            return defpath;
        }
    }
}