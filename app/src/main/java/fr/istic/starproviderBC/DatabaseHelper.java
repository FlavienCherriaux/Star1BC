package fr.istic.starproviderBC;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 17012154 on 14/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbStarBC";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_COUNTRY = "country";
    private static final String TABLE_CITY = "city";
    private static final String DATABASE_CREATE_1 = "CREATE TABLE IF NOT EXISTS " + TABLE_COUNTRY + " (_id integer primary key not null, name text)";
    private static final String DATABASE_CREATE_2 = "CREATE TABLE IF NOT EXISTS " + TABLE_CITY + " (_id integer primary key not null, country_id integer not null, name text)";

    public DatabaseHelper(Context context) {
        // Création ou ouverture de la base de données
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS busroute (_id integer primary key not null, route_short_name)");
        db.execSQL(DATABASE_CREATE_1);
        db.execSQL(DATABASE_CREATE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        onCreate(db);
    }

    // Test insertion puis affichage de la table Country
    public void test() {
        ContentValues values = new ContentValues();

        try {
            values.put("_id", 1);
            values.put("name", "France");
            this.getWritableDatabase().insert(TABLE_COUNTRY, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cursor c = this.getReadableDatabase().query(TABLE_COUNTRY, new String[] {"name"}, null, null, null, null, null);

        if (c != null) {
            while (!c.isLast()) {
                c.moveToNext();
                System.out.println(c.getString(0));
            }
        }

        this.getWritableDatabase().delete(TABLE_COUNTRY, null, null);
    }
}
