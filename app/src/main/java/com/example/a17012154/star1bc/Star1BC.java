package com.example.a17012154.star1bc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import fr.istic.starproviderBC.*;

public class Star1BC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star1_bc);

        // Récupération du fichier JSON contenant les noms des fichiers à télécharger pour remplir la base de données
        String versionsFilename = "versions.txt";
        File versionsFile = downloadFileFromWeb("https://data.explore.star.fr/explore/dataset/tco-busmetro-horaires-gtfs-versions-td/download/?format=json&timezone=Europe/Berlin", versionsFilename);

        try {
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
                String destFilename = (String) ((JSONObject) obj.get("fichier")).get("filename");

                // Si le fichier à télécharger n'existe pas déjà, c-à-d si les données ne sont pas à jour
                //if (!new File(this.getFilesDir().getAbsolutePath() + File.separator + destFilename).exists()) {
                    String sourceUrl = (String) obj.get("url");
                    File newZip = downloadFileFromWeb(sourceUrl, destFilename);
                    File destFolder = FileUnziper.unzip(newZip, new File(newZip.getParent() + File.separator + "data"));

                    // Insertion dans la base de données
                    DatabaseHelper dbh = new DatabaseHelper(this.getApplicationContext());
                    for (File f : destFolder.listFiles()) {
                        switch (f.getName()) {
                            case "routes.txt":

                                break;
                        }
                    }
                /*} else {
                    System.out.println("le fichier est déjà à jour");
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File downloadFileFromWeb(String url, String destFile) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        /*
        * il faut tester d’abord la connexion internet
        */
        if (networkInfo != null && networkInfo.isConnected()) {
            DataDownloader dd = new DataDownloader(this, destFile);
            dd.execute(url);

            return new File(this.getFilesDir().getAbsolutePath() + File.separator + destFile);
        } else {
            Toast.makeText(this.getApplicationContext(), "Connection to Internet failed", Toast.LENGTH_LONG);
            return null;
        }
    }
}
