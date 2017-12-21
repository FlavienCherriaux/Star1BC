package com.example.a17012154.star1bc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.istic.starproviderBC.DataDownloader;
import fr.istic.starproviderBC.DataSource;
import fr.istic.starproviderBC.FileUnzipper;

public class Star1BC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star1_bc);

        final DataSource ds = new DataSource(this);

        // Si l'application n'a pas été lancée depuis une notification
        if (!this.getIntent().getBooleanExtra("notification", false)) {
            // Si la base de données n'est pas à jour
            if (!ds.isUpToDate()) {
                String currentDataFileName = ds.getCurrentDataFileName();

                // Si le fichier contenant les données actuelles n'a pas encore été téléchargé
                if (currentDataFileName == null) {
                    // On récupère le fichier JSON contenant les noms des fichiers à télécharger pour remplir la base de données
                    final String versionsFilename = "versions.txt";
                    downloadFileFromWeb("https://data.explore.star.fr/explore/dataset/tco-busmetro-horaires-gtfs-versions-td/download/?format=json&timezone=Europe/Berlin", versionsFilename, new DataDownloader.DownloadListener() {
                        @Override
                        public void onDownloadCompleted() {
                            try {
                                // On récupère les données du fichier que l'on vient de télécharger
                                FileInputStream versions = Star1BC.this.openFileInput(versionsFilename);
                                byte[] bytes = new byte[versions.available()];
                                versions.read(bytes);
                                String content = new String(bytes);

                                // On cherche le fichier contenant les informations actuelles
                                JSONArray toJson = new JSONArray(content);
                                JSONObject obj = null;
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                Date today = new Date(), validityStart = new Date(), validityEnd = new Date();

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

                                    // On crée une notification
                                    Bundle extras = new Bundle();
                                    extras.putBoolean("notification", true);
                                    extras.putString("url", (String) obj.get("url"));
                                    extras.putString("destFilename", destFilename);
                                    createNotification(extras);

                                    ds.addVersion(destFilename, validityStart, validityEnd);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else { // Les données ont déjà été téléchargées mais n'ont pas été insérées dans la base
                    // On se contente de créer une notification sans re-télécharger les données
                    Bundle extras = new Bundle();
                    extras.putBoolean("notification", true);
                    extras.putString("destFilename", currentDataFileName);
                    createNotification(extras);
                }
            }
        } else { // L'application a été lancée depuis la notification permettant la mise à jour de la base de données
            final Bundle extras = this.getIntent().getExtras();

            DataDownloader.DownloadListener dl = new DataDownloader.DownloadListener() {
                @Override
                public void onDownloadCompleted() {
                    File newZip = new File(Star1BC.this.getFilesDir().getAbsolutePath() + File.separator + extras.getString("destFilename"));
                    File destFolder = FileUnzipper.unzip(newZip, new File(newZip.getParent() + File.separator + "data"));
                    ds.insert(destFolder);
                }
            };

            // Si le fichier n'a pas déjà été téléchargé
            if (extras.containsKey("url")) {
                downloadFileFromWeb(extras.getString("url"), extras.getString("destFilename"), dl);
            } else { // Sinon, on se contente de dézipper le fichier et d'insérer dans la base de données
                dl.onDownloadCompleted();
            }
        }
    }

    public void downloadFileFromWeb(String url, String destFile, DataDownloader.DownloadListener listener) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Il faut tester d’abord la connexion Internet
        if (networkInfo != null && networkInfo.isConnected()) {
            DataDownloader dd = new DataDownloader(this, destFile, listener);
            dd.execute(url);
        } else {
            Toast.makeText(this.getApplicationContext(), "Connection to Internet failed", Toast.LENGTH_LONG).show();
        }
    }

    public void createNotification(Bundle extras) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, Star1BC.class);
        resultIntent.putExtras(extras);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New data available")
                .setContentText("New data are available for Star1BC")
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(1, mBuilder.build());
    }
}