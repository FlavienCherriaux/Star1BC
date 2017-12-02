package fr.istic.starproviderBC;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import fr.istic.starproviderBC.StarContract.*;

/**
 * Created by 17012154 on 14/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbStarBC";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        // Création ou ouverture de la base de données
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création des tables

        // Table busroute
        db.execSQL("CREATE TABLE IF NOT EXISTS " + BusRoutes.CONTENT_PATH + " (" +
                BusRoutes.BusRouteColumns._ID + " INTEGER NOT NULL," +
                BusRoutes.BusRouteColumns.SHORT_NAME + " TEXT, " +
                BusRoutes.BusRouteColumns.LONG_NAME + " TEXT, " +
                BusRoutes.BusRouteColumns.DESCRIPTION + " TEXT, " +
                BusRoutes.BusRouteColumns.TYPE + " INTEGER, " +
                BusRoutes.BusRouteColumns.COLOR + " TEXT, " +
                BusRoutes.BusRouteColumns.TEXT_COLOR + " TEXT, " +
                "PRIMARY KEY(" + BusRoutes.BusRouteColumns._ID + ")" +
                ")");

        // Table trip
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Trips.CONTENT_PATH + " (" +
                Trips.TripColumns._ID + " INTEGER NOT NULL," +
                Trips.TripColumns.ROUTE_ID + " INTEGER, " +
                Trips.TripColumns.SERVICE_ID + " INTEGER, " +
                Trips.TripColumns.HEADSIGN + " TEXT, " +
                Trips.TripColumns.DIRECTION_ID + " INTEGER, " +
                Trips.TripColumns.BLOCK_ID + " TEXT, " +
                Trips.TripColumns.WHEELCHAIR_ACCESSIBLE + " INTEGER, " +
                "PRIMARY KEY(" + Trips.TripColumns._ID + ")" +
                ")");

        // Table stop
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Stops.CONTENT_PATH + " (" +
                Stops.StopColumns._ID + " INTEGER NOT NULL," +
                Stops.StopColumns.NAME + " TEXT, " +
                Stops.StopColumns.DESCRIPTION + " TEXT, " +
                Stops.StopColumns.LATITUDE + " REAL, " +
                Stops.StopColumns.LONGITUDE + " REAL, " +
                Stops.StopColumns.WHEELCHAIR_BOARDING + " INTEGER, " +
                "PRIMARY KEY(" + Stops.StopColumns._ID + ")" +
                ")");

        // Table stoptime
        db.execSQL("CREATE TABLE IF NOT EXISTS " + StopTimes.CONTENT_PATH + " (" +
                StopTimes.StopTimeColumns._ID + " INTEGER NOT NULL," +
                StopTimes.StopTimeColumns.TRIP_ID + " INTEGER, " +
                StopTimes.StopTimeColumns.ARRIVAL_TIME + " TEXT, " +
                StopTimes.StopTimeColumns.DEPARTURE_TIME + " TEXT, " +
                StopTimes.StopTimeColumns.STOP_ID + " INTEGER, " +
                StopTimes.StopTimeColumns.STOP_SEQUENCE + " INTEGER, " +
                "PRIMARY KEY(" + StopTimes.StopTimeColumns._ID + ")" +
                ")");

        // Table calendar
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Calendar.CONTENT_PATH + " (" +
                Calendar.CalendarColumns._ID + " INTEGER NOT NULL," +
                Calendar.CalendarColumns.MONDAY + " INTEGER, " +
                Calendar.CalendarColumns.TUESDAY + " INTEGER, " +
                Calendar.CalendarColumns.WEDNESDAY + " INTEGER, " +
                Calendar.CalendarColumns.THURSDAY + " INTEGER, " +
                Calendar.CalendarColumns.FRIDAY + " INTEGER, " +
                Calendar.CalendarColumns.SATURDAY + " INTEGER, " +
                Calendar.CalendarColumns.SUNDAY + " INTEGER, " +
                Calendar.CalendarColumns.START_DATE + " TEXT, " +
                Calendar.CalendarColumns.END_DATE + " TEXT, " +
                "PRIMARY KEY(" + Calendar.CalendarColumns._ID + ")" +
                ")");
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
        /*ContentValues values = new ContentValues();

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

        this.getWritableDatabase().delete(TABLE_COUNTRY, null, null);*/
    }
}
