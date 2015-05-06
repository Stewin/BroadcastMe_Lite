package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by mike on 28/04/2015.
 */
public class DownloadTask extends AsyncTask<String, Void, String> {

    private Context context;

    public DownloadTask(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(String... strings) {
        String link = strings[0];
        URL url = null;
        String webPage = "";
        HttpURLConnection conn = null;
        try {
            url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(10000); // millis
            conn.setConnectTimeout(15000); // millis
            conn.setDoOutput(true);

            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String data = "";
                while ((data = reader.readLine()) != null) {
                    webPage += data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return webPage;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Toast.makeText(context, "Toast inside Downloadtask", Toast.LENGTH_LONG).show();
    }


}
