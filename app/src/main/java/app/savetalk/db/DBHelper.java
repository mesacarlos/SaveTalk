package app.savetalk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    /**
     * Nombre y version de la base de datos
     */
    private static final String DATABASE_NAME = "SaveTalk.db";
    public static final int DATABASE_VERSION = 1;
    /**
     * Nombre de la tabla cola y sus columnas
     */
    public static final String TABLE_QUEUE = "cola";

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_FILENAME = "FILENAME";
    public static final String COLUMN_FTP = "FTP_UPLOADED";
    public static final String COLUMN_GDRIVE = "GDRIVE_UPLOADED";
    public static final String COLUMN_CLOUD = "CLOUD_UPLOADED";

    /**
     * Script para crear la base datos
     */
    private static final String DATABASE_CREATE_QUEUE = "create table " + TABLE_QUEUE + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FILENAME + " text, "
            + COLUMN_FTP + " integer not null, "
            + COLUMN_GDRIVE + " integer not null, "
            + COLUMN_CLOUD + " integer not null "
            + ");";

    /**
     * Script para borrar la base de datos
     */
    private static final String DATABASE_DROP_QUEUE = "DROP TABLE IF EXISTS " + TABLE_QUEUE;


    public DBHelper(Context contexto, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //invocacamos execSQL pq no devuelve ningún tipo de dataset
        db.execSQL(DATABASE_CREATE_QUEUE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DROP_QUEUE);
        this.onCreate(db);
    }
}