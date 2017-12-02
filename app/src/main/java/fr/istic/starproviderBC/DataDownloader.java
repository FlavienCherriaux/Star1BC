package fr.istic.starproviderBC;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by 17012154 on 21/11/17.
 */

public class DataDownloader extends AsyncTask<String, Integer, byte[]> {
    private ProgressDialog dialog;
    private String file;
    private Activity activity;
    private DownloadListener listener;

    public interface DownloadListener {
        void onDownloadCompleted();
    }

    public DataDownloader(Activity activity, String filename, DownloadListener listener) {
        this.dialog = new ProgressDialog(activity);
        this.file = filename;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Download in progress");
        dialog.show();
    }

    @Override
    protected byte[] doInBackground(String... params) {
        DataInputStream open = null;
        try {
            URL url = new URL(params[0]);
            open = new DataInputStream(url.openStream());
            return readIt(open);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (open != null) {
                try {
                    open.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new byte[0];
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        System.out.println(Arrays.toString(values));
    }

    @Override
    protected void onPostExecute(byte[] result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        try {
            FileOutputStream outputStream = activity.openFileOutput(file, Context.MODE_PRIVATE);
            outputStream.write(result);
            outputStream.flush();
            outputStream.close();
            listener.onDownloadCompleted();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readIt(DataInputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toByteArray();
    }
}
