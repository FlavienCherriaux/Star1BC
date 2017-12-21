package fr.istic.starproviderBC;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        this.progressBar = new ProgressBar(activity);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(File... files) {
        // Liste des fichiers auxquels on associe le nom de la table correspondante dans la base de données
        Map<String, Class> fileTableAssociations = new HashMap<String, Class>();
        fileTableAssociations.put("routes.txt", StarContract.BusRoutes.class);
        fileTableAssociations.put("trips.txt", StarContract.Trips.class);
        fileTableAssociations.put("stops.txt", StarContract.Stops.class);
        fileTableAssociations.put("stop_times.txt", StarContract.StopTimes.class);
        fileTableAssociations.put("calendar.txt", StarContract.Calendar.class);

        String table;
        ContentValues values;

        for (File f : files[0].listFiles()) {
            // Si le fichier est un fichier utile
            if (fileTableAssociations.containsKey(f.getName())) {
                table = fileTableAssociations.get(f.getName());
                publishProgress(0);

                try {
                    CSVParser parser = new CSVParser(new FileReader(f), CSVFormat.DEFAULT);
                    progressBar.setMax(parser.getRecords().size());

                    for (CSVRecord record : parser.getRecords()) {
                        values = new ContentValues();

                        switch (f.getName()) {
                            case "routes.txt":
                                values.put(StarContract.BusRoutes.BusRouteColumns._ID, record.get(StarContract.BusRoutes.BusRouteColumns._ID));
                                values.put(StarContract.BusRoutes.BusRouteColumns.SHORT_NAME, splitedStringValues[2]);
                                values.put(StarContract.BusRoutes.BusRouteColumns.LONG_NAME, splitedStringValues[3]);
                                values.put(StarContract.BusRoutes.BusRouteColumns.DESCRIPTION, splitedStringValues[4]);
                                values.put(StarContract.BusRoutes.BusRouteColumns.TYPE, splitedStringValues[5]);
                                values.put(StarContract.BusRoutes.BusRouteColumns.COLOR, splitedStringValues[7]);
                                values.put(StarContract.BusRoutes.BusRouteColumns.TEXT_COLOR, splitedStringValues[8]);
                                break;
                            case "trips.txt":
                                values.put(StarContract.Trips.TripColumns._ID, splitedStringValues[2]);
                                values.put(StarContract.Trips.TripColumns.ROUTE_ID, splitedStringValues[0]);
                                values.put(StarContract.Trips.TripColumns.SERVICE_ID, splitedStringValues[1]);
                                values.put(StarContract.Trips.TripColumns.HEADSIGN, splitedStringValues[3]);
                                values.put(StarContract.Trips.TripColumns.DIRECTION_ID, splitedStringValues[5]);
                                values.put(StarContract.Trips.TripColumns.BLOCK_ID, splitedStringValues[6]);
                                values.put(StarContract.Trips.TripColumns.WHEELCHAIR_ACCESSIBLE, splitedStringValues[8]);
                                break;
                            case "stops.txt":
                                values.put(StarContract.Stops.StopColumns._ID, splitedStringValues[0]);
                                values.put(StarContract.Stops.StopColumns.NAME, splitedStringValues[2]);
                                values.put(StarContract.Stops.StopColumns.DESCRIPTION, splitedStringValues[3]);
                                values.put(StarContract.Stops.StopColumns.LATITUDE, Float.valueOf(splitedStringValues[4]));
                                values.put(StarContract.Stops.StopColumns.LONGITUDE, Float.valueOf(splitedStringValues[5]));
                                values.put(StarContract.Stops.StopColumns.WHEELCHAIR_BOARDING, splitedStringValues[11]);
                                break;
                            case "stop_times.txt":
                                values.put(StarContract.StopTimes.StopTimeColumns.TRIP_ID, splitedStringValues[0]);
                                values.put(StarContract.StopTimes.StopTimeColumns.ARRIVAL_TIME, splitedStringValues[1]);
                                values.put(StarContract.StopTimes.StopTimeColumns.DEPARTURE_TIME, splitedStringValues[2]);
                                values.put(StarContract.StopTimes.StopTimeColumns.STOP_ID, splitedStringValues[3]);
                                values.put(StarContract.StopTimes.StopTimeColumns.STOP_SEQUENCE, splitedStringValues[4]);
                                break;
                            case "calendar.txt":
                                values.put(StarContract.Calendar.CalendarColumns._ID, splitedStringValues[0]);
                                values.put(StarContract.Calendar.CalendarColumns.MONDAY, splitedStringValues[1]);
                                values.put(StarContract.Calendar.CalendarColumns.TUESDAY, splitedStringValues[2]);
                                values.put(StarContract.Calendar.CalendarColumns.WEDNESDAY, splitedStringValues[3]);
                                values.put(StarContract.Calendar.CalendarColumns.THURSDAY, splitedStringValues[4]);
                                values.put(StarContract.Calendar.CalendarColumns.FRIDAY, splitedStringValues[5]);
                                values.put(StarContract.Calendar.CalendarColumns.SATURDAY, splitedStringValues[6]);
                                values.put(StarContract.Calendar.CalendarColumns.SUNDAY, splitedStringValues[7]);
                                values.put(StarContract.Calendar.CalendarColumns.START_DATE, splitedStringValues[8]);
                                values.put(StarContract.Calendar.CalendarColumns.END_DATE, splitedStringValues[9]);
                                break;
                            default:
                                break;
                        }

                        if (values.size() > 0) {
                            database.insert(table, null, values);
                            publishProgress((int) parser.getRecordNumber());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Scanner sc = new Scanner(f);

                    // Si le fichier n'est pas vide
                    if (sc.hasNextLine()) {
                        // La première ligne du fichier correspond aux en-têtes du fichier,
                        // il faut donc passer cette ligne-là avant de réaliser les insertions
                        sc.nextLine();
                        String nextLine;

                        while (sc.hasNextLine()) {
                            // On récupère la prochaine ligne du fichier
                            nextLine = sc.nextLine();

                            // Si la ligne n'est pas vide (car chaque fichier contient une ligne vide à la fin)
                            if (!nextLine.isEmpty()) {
                                // On supprime le premier et le dernier guillemet de la ligne ...
                                nextLine = nextLine.substring(1, nextLine.length() - 1);
                                // ... puis on explose le résultat avec le séparateur ","
                                String[] splitedStringValues = nextLine.split("\",\"");

                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
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