package app.savetalk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla Queue
 * Se encarga de abrir y cerrar la conexion, asi como hacer las consultas relacionadas con la cola
 */
public class ElementDataSource {
    /**
     * Referencia para manejar la base de datos. Este objeto lo obtenemos a partir de DBHelper
     * y nos proporciona metodos para hacer operaciones
     * CRUD (create, read, update and delete)
     */
    private SQLiteDatabase database;

    /**
     * Referencia al helper que se encarga de crear y actualizar la base de datos (cola).
     */
    private DBHelper dbHelper;

    /**
     * Columnas de la tabla
     */
    private final String[] allColumnsQueue = { DBHelper.COLUMN_ID, DBHelper.COLUMN_FILENAME,
            DBHelper.COLUMN_FTP, DBHelper.COLUMN_GDRIVE, DBHelper.COLUMN_CLOUD};

    /**
     * Constructor de la clase ElementDataSource.
     */
    public ElementDataSource(Context context) {
        dbHelper = new DBHelper(context, null, null, DBHelper.DATABASE_VERSION);
    }

    /**
     * Abre una conexion para escritura con la base de datos.
     * Esto lo hace a traves del helper con la llamada a getWritableDatabase. Si la base de
     * datos no esta creada, el helper se encargara de llamar a onCreate
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Cierra la conexion con la base de datos
     */
    public void close() {
        dbHelper.close();
    }

    public long insertQueueElement(QueueElement elemToInsert) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FILENAME, elemToInsert.getFilePath());
        values.put(DBHelper.COLUMN_FTP, boolToInt(elemToInsert.isFTP_UPLOADED()));
        values.put(DBHelper.COLUMN_GDRIVE, boolToInt(elemToInsert.isGDRIVE_UPLOADED()));
        values.put(DBHelper.COLUMN_CLOUD, boolToInt(elemToInsert.isCLOUD_UPLOADED()));

        // Insertamos la serie
        long insertId = database.insert(DBHelper.TABLE_QUEUE, null, values);
        return insertId;
    }

    /**
     * Obtiene todos los elementos registrados
     *
     * @return Lista de elementos grabados
     */
    public List<QueueElement> getAllQueueElements() {
        List<QueueElement> elementList = new ArrayList<QueueElement>();

        Cursor cursor = database.query(DBHelper.TABLE_QUEUE, allColumnsQueue, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            QueueElement elem = new QueueElement();
            elem.setID(cursor.getInt(0));
            elem.setFilePath(cursor.getString(1));
            elem.setFTP_UPLOADED(intToBool(cursor.getInt(2)));
            elem.setGDRIVE_UPLOADED(intToBool(cursor.getInt(3)));
            elem.setCLOUD_UPLOADED(intToBool(cursor.getInt(4)));

            elementList.add(elem);
            cursor.moveToNext();
        }
        cursor.close();
        return elementList;
    }

    public void removeElement(Integer elementID) {
        database.delete(DBHelper.TABLE_QUEUE, DBHelper.COLUMN_ID + "==" + elementID, null);
    }

    public void marcarElementoSubidoFTP(Integer idElemento, boolean subido) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FTP, subido);
        database.update(DBHelper.TABLE_QUEUE, values, DBHelper.COLUMN_ID + "==" + idElemento,null);
    }

    public void marcarElementoSubidoGDrive(Integer idElemento, boolean subido) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_GDRIVE, subido);
        database.update(DBHelper.TABLE_QUEUE, values, DBHelper.COLUMN_ID + "==" + idElemento,null);
    }

    public void marcarElementoSubidoCloud(Integer idElemento, boolean subido) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CLOUD, subido);
        database.update(DBHelper.TABLE_QUEUE, values, DBHelper.COLUMN_ID + "==" + idElemento,null);
    }

    private int boolToInt(boolean bool){
        return bool ? 1 : 0;
    }

    private boolean intToBool(int i){
        return i==1;
    }
}