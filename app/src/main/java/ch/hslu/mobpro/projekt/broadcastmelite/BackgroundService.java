package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.*;


/**
 * BackgroundService für prüfen von Nachrichten
 */
public class BackgroundService extends Service {
    private static final int MY_NOTIFICATION_ID=1;
    private Context context;
    private int countNewMessages = 0;

    public void displayNotification(String title, String text){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.logo_broadcastmelite);

        Intent notificationIntent = new Intent(this, MainActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            DownloadTask performBackgroundTask = new DownloadTask(context);
                            String result = performBackgroundTask.execute("http://mikegernet.ch/mobpro/index.php?get=1234&timestamp=1").get();
                            int newMessages = parseJSON(result);
                            if(newMessages>countNewMessages){
                                displayNotification("BroadcastMe Lite", "Sie haben " + newMessages + " neue Nachrichten");
                                countNewMessages = newMessages;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        this.context = this;
        callAsynchronousTask();

        //Toast.makeText(this, "Service gestartet", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Kein Binding implementiert (Braucht es auch nicht, da dies nur für gebundene Services gebraucht würde)
        return null;
    }

    public int parseJSON(String json) {
        int result = 0;
        try {
            JSONArray jsonArray = new JSONArray(json);
            result = jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

        /*
        //Funktioniert falls Daten extrahiert werden müssen
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String timestamp = jsonObject.getString("timestamp");
            String message = jsonObject.getString("message");
            System.err.println(timestamp +": " + message);
        }*/
    }


}
