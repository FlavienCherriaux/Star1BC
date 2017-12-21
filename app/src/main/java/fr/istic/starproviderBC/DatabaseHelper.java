package fr.istic.starproviderBC;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import fr.istic.starproviderBC.StarContract.BusRoutes;
import fr.istic.starproviderBC.StarContract.Calendar;
import fr.istic.starproviderBC.StarContract.StopTimes;
import fr.istic.starproviderBC.StarContract.Stops;
import fr.istic.starproviderBC.StarContract.Trips;

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
                BusRoutes.BusRouteColumns._ID + " TEXT NOT NULL, " +
                BusRoutes.BusRouteColumns.SHORT_NAME + " TEXT, " +
                BusRoutes.BusRouteColumns.LONG_NAME + " TEXT, " +
                BusRoutes.BusRouteColumns.DESCRIPTION + " TEXT, " +
                BusRoutes.BusRouteColumns.TYPE + " TEXT, " +
                BusRoutes.BusRouteColumns.COLOR + " TEXT, " +
                BusRoutes.BusRouteColumns.TEXT_COLOR + " TEXT, " +
                "PRIMARY KEY(" + BusRoutes.BusRouteColumns._ID + ")" +
                ")");

        // Table trip
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Trips.CONTENT_PATH + " (" +
                Trips.TripColumns._ID + " TEXT NOT NULL, " +
                Trips.TripColumns.ROUTE_ID + " TEXT, " +
                Trips.TripColumns.SERVICE_ID + " TEXT, " +
                Trips.TripColumns.HEADSIGN + " TEXT, " +
                Trips.TripColumns.DIRECTION_ID + " TEXT, " +
                Trips.TripColumns.BLOCK_ID + " TEXT, " +
                Trips.TripColumns.WHEELCHAIR_ACCESSIBLE + " TEXT, " +
                "PRIMARY KEY(" + Trips.TripColumns._ID + ")" +
                ")");

        // Table stop
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Stops.CONTENT_PATH + " (" +
                Stops.StopColumns._ID + " TEXT NOT NULL, " +
                Stops.StopColumns.NAME + " TEXT, " +
                Stops.StopColumns.DESCRIPTION + " TEXT, " +
                Stops.StopColumns.LATITUDE + " REAL, " +
                Stops.StopColumns.LONGITUDE + " REAL, " +
                Stops.StopColumns.WHEELCHAIR_BOARDING + " TEXT, " +
                "PRIMARY KEY(" + Stops.StopColumns._ID + ")" +
                ")");

        // Table stoptime
        db.execSQL("CREATE TABLE IF NOT EXISTS " + StopTimes.CONTENT_PATH + " (" +
                StopTimes.StopTimeColumns._ID + " TEXT NOT NULL," +
                StopTimes.StopTimeColumns.TRIP_ID + " TEXT, " +
                StopTimes.StopTimeColumns.ARRIVAL_TIME + " TEXT, " +
                StopTimes.StopTimeColumns.DEPARTURE_TIME + " TEXT, " +
                StopTimes.StopTimeColumns.STOP_ID + " TEXT, " +
                StopTimes.StopTimeColumns.STOP_SEQUENCE + " TEXT, " +
                "PRIMARY KEY(" + StopTimes.StopTimeColumns._ID + ")" +
                ")");

        // Table calendar
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Calendar.CONTENT_PAT + " (" +
                Calendar.CalendarColumns._ID + " TEXT NOT NULL, " +
                Calendar.CalendarColumns.MONDAY + " TEXT, " +
                Calendar.CalendarColumns.TUESDAY + " TEXT, " +
                Calendar.CalendarColumns.WEDNESDAY + " TEXT, " +
                Calendar.CalendarColumns.THURSDAY + " TEXT, " +
                Calendar.CalendarColumns.FRIDAY + " TEXT, " +
                Calendar.CalendarColumns.SATURDAY + " TEXT, " +
                Calendar.CalendarColumns.SUNDAY + " TEXT, " +
                Calendar.CalendarColumns.START_DATE + " TEXT, " +
                Calendar.CalendarColumns.END_DATE + " TEXT, " +
                "PRIMARY KEY(" + Calendar.CalendarColumns._ID + ")" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS versions (" +
                "filename TEXT, " + // nom du fichier zip contenant les données
                "validityStart TEXT, " + // date de début de validité du fichier
                "validityEnd TEXT, " + // date de fin de validité du fichier
                "dataInserted INTEGER, " + // 0 si les données n'ont pas été insérées dans la base, 1 sinon (booléen)
                "PRIMARY KEY (filename)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE " + BusRoutes.CONTENT_PATH);
        db.execSQL("DROP TABLE " + Trips.CONTENT_PATH);
        db.execSQL("DROP TABLE " + Stops.CONTENT_PATH);
        db.execSQL("DROP TABLE " + StopTimes.CONTENT_PATH);
        db.execSQL("DROP TABLE " + Calendar.CONTENT_PAT);
        onCreate(db);
    }
}
