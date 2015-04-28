package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * BackgroundService für prüfen von Nachrichten
 */
public class BackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);



        Toast.makeText(this, "testmessage", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Kein Binding implementiert (Braucht es auch nicht, da dies nur für gebundene Services gebraucht würde)
        return null;
    }
}
