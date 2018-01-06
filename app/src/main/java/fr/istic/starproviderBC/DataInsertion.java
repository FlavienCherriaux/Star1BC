package fr.istic.starproviderBC;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a17012154.star1bc.R;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.istic.starproviderBC.StarContract.BusRoutes;
import fr.istic.starproviderBC.StarContract.Calendar;
import fr.istic.starproviderBC.StarContract.StopTimes;
import fr.istic.starproviderBC.StarContract.Stops;
import fr.istic.starproviderBC.StarContract.Trips;

/**
 * fr.istic.starproviderBC
 * Created by Flavien on 19/12/2017.
 */

public class DataInsertion extends AsyncTask<File, Integer, Boolean> {
    private Activity activity;
    private SQLiteDatabase database;
    private ProgressBar progressBar;

    public DataInsertion(Activity activity, SQLiteDatabase database) {
        this.activity = activity;
        this.database = database;
        this.progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
    }

    @Override
    protected void onPreExecute() {
        progressBar.setMax(100);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(File... files) {
        // Liste des fichiers auxquels on associe le nom de la table correspondante dans la base de données
        Map<String, String> fileTableAssociations = new HashMap<String, String>();
        fileTableAssociations.put("routes.txt", BusRoutes.CONTENT_PATH);
        fileTableAssociations.put("trips.txt", Trips.CONTENT_PATH);
        fileTableAssociations.put("stops.txt", Stops.CONTENT_PATH);
        fileTableAssociations.put("stop_times.txt", StopTimes.CONTENT_PATH);
        fileTableAssociations.put("calendar.txt", Calendar.CONTENT_PATH);

        long fileLength;
        String table;
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        CSVParser parser;
        ContentValues values;

        for (File f : files[0].listFiles()) {
            // Si le fichier est un fichier utile
            if (fileTableAssociations.containsKey(f.getName())) {
                fileLength = f.length();
                table = fileTableAssociations.get(f.getName());
                publishProgress(0);

                try {
                    parser = new CSVParser(new FileReader(f), format);

                    for (CSVRecord record : parser) {
                        publishProgress((int) (progressBar.getMax() * record.getCharacterPosition() / fileLength));

                        values = new ContentValues();

                        switch (f.getName()) {
                            case "routes.txt":
                                values.put(BusRoutes.BusRouteColumns._ID, record.get(BusRoutes.BusRouteColumns._ID));
                                values.put(BusRoutes.BusRouteColumns.SHORT_NAME, record.get(BusRoutes.BusRouteColumns.SHORT_NAME));
                                values.put(BusRoutes.BusRouteColumns.LONG_NAME, record.get(BusRoutes.BusRouteColumns.LONG_NAME));
                                values.put(BusRoutes.BusRouteColumns.DESCRIPTION, record.get(BusRoutes.BusRouteColumns.DESCRIPTION));
                                values.put(BusRoutes.BusRouteColumns.TYPE, record.get(BusRoutes.BusRouteColumns.TYPE));
                                values.put(BusRoutes.BusRouteColumns.COLOR, record.get(BusRoutes.BusRouteColumns.COLOR));
                                values.put(BusRoutes.BusRouteColumns.TEXT_COLOR, record.get(BusRoutes.BusRouteColumns.TEXT_COLOR));
                                break;
                            case "trips.txt":
                                values.put(Trips.TripColumns._ID, record.get(Trips.TripColumns._ID));
                                values.put(Trips.TripColumns.ROUTE_ID, record.get(Trips.TripColumns.ROUTE_ID));
                                values.put(Trips.TripColumns.SERVICE_ID, record.get(Trips.TripColumns.SERVICE_ID));
                                values.put(Trips.TripColumns.HEADSIGN, record.get(Trips.TripColumns.HEADSIGN));
                                values.put(Trips.TripColumns.DIRECTION_ID, record.get(Trips.TripColumns.DIRECTION_ID));
                                values.put(Trips.TripColumns.BLOCK_ID, record.get(Trips.TripColumns.BLOCK_ID));
                                values.put(Trips.TripColumns.WHEELCHAIR_ACCESSIBLE, record.get(Trips.TripColumns.WHEELCHAIR_ACCESSIBLE));
                                break;
                            case "stops.txt":
                                values.put(Stops.StopColumns._ID, record.get(Stops.StopColumns._ID));
                                values.put(Stops.StopColumns.NAME, record.get(Stops.StopColumns.NAME));
                                values.put(Stops.StopColumns.DESCRIPTION, record.get(Stops.StopColumns.DESCRIPTION));
                                values.put(Stops.StopColumns.LATITUDE, Float.valueOf(record.get(Stops.StopColumns.LATITUDE)));
                                values.put(Stops.StopColumns.LONGITUDE, Float.valueOf(record.get(Stops.StopColumns.LONGITUDE)));
                                values.put(Stops.StopColumns.WHEELCHAIR_BOARDING, record.get(Stops.StopColumns.WHEELCHAIR_BOARDING));
                                break;
                            case "stop_times.txt":
                                values.put(StopTimes.StopTimeColumns.TRIP_ID, record.get(StopTimes.StopTimeColumns.TRIP_ID));
                                values.put(StopTimes.StopTimeColumns.ARRIVAL_TIME, record.get(StopTimes.StopTimeColumns.ARRIVAL_TIME));
                                values.put(StopTimes.StopTimeColumns.DEPARTURE_TIME, record.get(StopTimes.StopTimeColumns.DEPARTURE_TIME));
                                values.put(StopTimes.StopTimeColumns.STOP_ID, record.get(StopTimes.StopTimeColumns.STOP_ID));
                                values.put(StopTimes.StopTimeColumns.STOP_SEQUENCE, record.get(StopTimes.StopTimeColumns.STOP_SEQUENCE));
                                break;
                            case "calendar.txt":
                                values.put(Calendar.CalendarColumns._ID, record.get(Calendar.CalendarColumns._ID));
                                values.put(Calendar.CalendarColumns.MONDAY, record.get(Calendar.CalendarColumns.MONDAY));
                                values.put(Calendar.CalendarColumns.TUESDAY, record.get(Calendar.CalendarColumns.TUESDAY));
                                values.put(Calendar.CalendarColumns.WEDNESDAY, record.get(Calendar.CalendarColumns.WEDNESDAY));
                                values.put(Calendar.CalendarColumns.THURSDAY, record.get(Calendar.CalendarColumns.THURSDAY));
                                values.put(Calendar.CalendarColumns.FRIDAY, record.get(Calendar.CalendarColumns.FRIDAY));
                                values.put(Calendar.CalendarColumns.SATURDAY, record.get(Calendar.CalendarColumns.SATURDAY));
                                values.put(Calendar.CalendarColumns.SUNDAY, record.get(Calendar.CalendarColumns.SUNDAY));
                                values.put(Calendar.CalendarColumns.START_DATE, record.get(Calendar.CalendarColumns.START_DATE));
                                values.put(Calendar.CalendarColumns.END_DATE, record.get(Calendar.CalendarColumns.END_DATE));
                                break;
                            default:
                                break;
                        }

                        if (values.size() > 0) {
                            if (database.insert(table, null, values) == -1) {
                                // Erreur lors de l'insertion,
                                // on stoppe tout pour éviter de n'avoir que des données partielles dans la base
                                return false;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                System.err.println("table " + table + " terminée");
            }
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            ContentValues newValues = new ContentValues();
            newValues.put("dataInserted", 1);
            database.update("versions", newValues, "? BETWEEN validityStart AND validityEnd", new String[] {new SimpleDateFormat("yyyy-MM-dd").format(new Date())});
        }
    }
}