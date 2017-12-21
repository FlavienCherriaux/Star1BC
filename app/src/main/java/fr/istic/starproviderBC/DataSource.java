package fr.istic.starproviderBC;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 17012154 on 05/12/17.
 */

public class DataSource {
    // cut -d',' -f5 routes.txt |sort|uniq
    private Activity activity;
    private DatabaseHelper dbHelper;

    public DataSource(Activity activity) {
        this.activity = activity;
        dbHelper = new DatabaseHelper(activity.getApplicationContext());
    }

    public void insert(File dataFolder) {
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
        new DataInsertion(activity, dbHelper.getWritableDatabase()).execute(dataFolder);
    }

    public boolean isUpToDate() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(new Date()); // date d'aujourd'hui au format : aaaa-MM-jj
        /*
        On compte le nombre de fichiers téléchargés dont l'intervalle de validité comprend la date actuelle
        et dont les données ont été insérées dans la base
        */
        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) " +
                "FROM versions " +
                "WHERE ? BETWEEN validityStart AND validityEnd " +
                "AND dataInserted = 1", new String[] {today});

        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        return count > 0;
    }

    public String getCurrentDataFileName() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(new Date()); // date d'aujourd'hui au format : aaaa-MM-jj
        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT filename " +
                "FROM versions " +
                "WHERE ? BETWEEN validityStart AND validityEnd", new String[] {today});

        String filename;
        if (c.moveToFirst()) {
            filename = c.getString(0);
        } else {
            filename = null;
        }

        c.close();
        return filename;
    }

    public void addVersion(String filename, Date validityStart, Date validityEnd) {
        ContentValues newValues = new ContentValues();
        newValues.put("filename", filename);
        newValues.put("validityStart", validityStart.toString());
        newValues.put("validityEnd", validityEnd.toString());
        newValues.put("dataInserted", 0);
        dbHelper.getWritableDatabase().insert("versions", null, newValues);
    }

    public void restart() {
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
    }

    public void close() {
        dbHelper.close();
    }
}