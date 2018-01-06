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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

    public DataSource(Activity activity) {
        this.activity = activity;
        dbHelper = new DatabaseHelper(activity.getApplicationContext());
    }

    public void insert(File dataFolder) {
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
        new DataInsertion(activity, dbHelper.getWritableDatabase()).execute(dataFolder);
    }

    /**
     * Détermine si la base de données est à jour
     * @return true si la base de données est à jour, false sinon
     */
    public boolean isUpToDate() {
        String today = dateFormat.format(new Date()); // date d'aujourd'hui au format : aaaa-MM-jj
        Cursor c = getCurrentDataInfo();
        return c != null && c.getInt(c.getColumnIndex("dataInserted")) == 1;
    }

    public Cursor getCurrentDataInfo() {
        String today = dateFormat.format(new Date()); // date d'aujourd'hui au format : aaaa-MM-jj
        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT * " +
                "FROM versions " +
                "WHERE ? BETWEEN validityStart AND validityEnd", new String[] {today});

        return c.moveToFirst() ? c : null;
    }

    public void addVersion(String filename, Date validityStart, Date validityEnd) {
        ContentValues newValues = new ContentValues();
        newValues.put("filename", filename);
        newValues.put("validityStart", dateFormat.format(validityStart));
        newValues.put("validityEnd", dateFormat.format(validityEnd));
        newValues.put("dataInserted", 0);
        dbHelper.getWritableDatabase().insert("versions", null, newValues);
    }

    public void removeVersions() {
        dbHelper.getWritableDatabase().execSQL("DELETE FROM versions");
    }

    public void printVersions() {
        Cursor c = dbHelper.getWritableDatabase().rawQuery("SELECT filename, validityStart, validityEnd, dataInserted FROM versions", null);
        while (c.moveToNext()) {
            System.out.println(c.getString(0));
            System.out.println(c.getString(1));
            System.out.println(c.getString(2));
            System.out.println(c.getInt(3));
        }
    }

    public void restart() {
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
    }

    public void close() {
        dbHelper.close();
    }
}