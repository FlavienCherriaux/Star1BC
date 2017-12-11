package com.example.a17012154.star1bc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import fr.istic.starproviderBC.*;

public class Star1BC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.err.println(this.fileList());
        System.err.println(this.getFilesDir());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star1_bc);

        // Si l'application n'a pas été lancée depuis une notification
        if (!this.getIntent().getBooleanExtra("notification", false)) {
            // Récupération du fichier JSON contenant les noms des fichiers à télécharger pour remplir la base de données
            final String versionsFilename = "versions.txt";
            downloadFileFromWeb("https://data.explore.star.fr/explore/dataset/tco-busmetro-horaires-gtfs-versions-td/download/?format=json&timezone=Europe/Berlin", versionsFilename, new DataDownloader.DownloadListener() {
                @Override
                public void onDownloadCompleted() {
                    try {
                        File versionsFile = new File(Star1BC.this.getFilesDir().getAbsolutePath() + File.separator + versionsFilename);
                        // On récupère les données du fichier que l'on vient de télécharger
                        Scanner sc = new Scanner(versionsFile);
                        String content = "";
                        while (sc.hasNext()) {
                            content += sc.next();
                        }

                        // On cherche le fichier contenant les informations actuelles
                        JSONArray toJson = new JSONArray(content);
                        JSONObject obj = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date today = new Date(), validityStart, validityEnd;

                        // Pour chaque fichier ...
                        for (int i = 0; i < toJson.length(); i++) {
                            obj = (JSONObject) ((JSONObject) toJson.get(i)).get("fields");
                            validityStart = df.parse((String) obj.get("debutvalidite"));
                            validityEnd = df.parse((String) obj.get("finvalidite"));

                            // ... on vérifie si la date d'aujourd'hui se trouve dans l'intervalle de validité
                            if (today.after(validityStart) && today.before(validityEnd)) {
                                break;
                            }

                            obj = null;
                        }

                        if (obj != null) {
                            final String destFilename = (String) ((JSONObject) obj.get("fichier")).get("filename");
                            File aze = new File(Star1BC.this.getFilesDir().getAbsolutePath() + File.separator + destFilename);
                            System.out.println("delete : " + (aze.delete() ? "ok" : "bite"));

                            // Si le fichier à télécharger n'existe pas déjà, c-à-d si les données ne sont pas à jour ...
                            if (/*!new File(Star1BC.this.getFilesDir().getAbsolutePath() + File.separator + destFilename).exists()*/aze.exists()) {
                                // ... on crée une notification
                                NotificationManager notificationManager = (NotificationManager) Star1BC.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                Intent resultIntent = new Intent(Star1BC.this, Star1BC.class);
                                resultIntent.putExtra("notification", true);
                                resultIntent.putExtra("url", (String) obj.get("url"));
                                resultIntent.putExtra("destFilename", destFilename);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(Star1BC.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Star1BC.this)
                                        .setSmallIcon(android.R.drawable.ic_menu_more)
                                        .setContentTitle("New content available")
                                        .setContentText("New data are available for Star1BC")
                                        .setAutoCancel(true)
                                        .setContentIntent(resultPendingIntent);
                                notificationManager.notify(123456, mBuilder.build());
                            } else {
                                System.out.println("le fichier est déjà à jour putain");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            // L'application a été lancée depuis la notification permettant la mise à jour de la base de données

            final Bundle extras = this.getIntent().getExtras();
            String sourceUrl = extras.getString("url");
            downloadFileFromWeb(sourceUrl, extras.getString("destFilename"), new DataDownloader.DownloadListener() {
                @Override
                public void onDownloadCompleted() {
                    File newZip = new File(Star1BC.this.getFilesDir().getAbsolutePath() + File.separator + extras.getString("destFilename"));
                    File destFolder = FileUnziper.unzip(newZip, new File(newZip.getParent() + File.separator + "data"));

                    // Insertion dans la base de données
                    DataSource ds = new DataSource(Star1BC.this.getApplicationContext(), destFolder);
                }
            });
        }
    }

    public void downloadFileFromWeb(String url, String destFile, DataDownloader.DownloadListener listener) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        /*
        * il faut tester d’abord la connexion internet
        */
        if (networkInfo != null && networkInfo.isConnected()) {
            DataDownloader dd = new DataDownloader(this, destFile, listener);
            dd.execute(url);
        } else {
            Toast.makeText(this.getApplicationContext(), "Connection to Internet failed", Toast.LENGTH_LONG);
        }
    }
}
