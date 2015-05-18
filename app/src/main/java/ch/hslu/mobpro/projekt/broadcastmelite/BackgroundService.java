package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


/**
 * BackgroundService für prüfen von Nachrichten
 */
public class BackgroundService extends Service {
    private static final int MY_NOTIFICATION_ID = 1;
    private final String INTERVALL_PREFERENCE = "intervall";
    private Context context;
    private int countNewMessages = 0;
    private SharedPreferences preference;

    public void displayNotification(String title, String text) {
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
        final Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            ArrayList<String> ids = getIdsFromMyMessages();
                            preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            long timestamp = preference.getLong("timestamp", Calendar.getInstance().getTimeInMillis() / 1000L);
                            int newMessages = 0;
                            for (String tmp : ids) {
                                DownloadTask performBackgroundTask = new DownloadTask(context);
                                String result = performBackgroundTask.execute("http://mikegernet.ch/mobpro/index.php?get=" + tmp + "&timestamp=" + timestamp).get();
                                Log.i("BroadcastMe", "String " + tmp + " wurde gelesen. Timestamp: " + timestamp + " Result: " + result);
                                newMessages += parseJSON(result);
                            }
                            if (newMessages > countNewMessages) {
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


        preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int intervall = preference.getInt(INTERVALL_PREFERENCE, 0);
        int intervallValue = 10000;
        switch (intervall) {
            case 1:
                intervallValue = 10;
                break;
            case 2:
                intervallValue = 60 * 5;
                break;
            case 3:
                intervallValue = 60 * 60;
                break;
            case 4:
                intervallValue = 60 * 60;
                break;
            case 5:
                intervallValue = 60 * 60 * 4;
                break;
            case 6:
                intervallValue = 60 * 60 * 12;
                break;
            case 7:
                intervallValue = 60 * 60 * 24;
                break;
            default:
                intervallValue = 60 * 60;
                break;
        }

        timer.schedule(doAsynchronousTask, 0, intervallValue * 1000);

    }


    private ArrayList<String> getIdsFromMyMessages() {
        ArrayList<String> strings = new ArrayList<>();
        File file = new File(getFilesDir() + "/mymessages/");
        if (file.exists()) {
            for (String strFile : file.list()) {
                //.txt nicht hinzufügen
                String tmp = strFile.substring(0, strFile.length() - 4);
                strings.add(tmp);
            }
        }
        return strings;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Log.i("BroadcastMe", "Service gestartet");
        this.context = this;
        callAsynchronousTask();
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
    }


}
