package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receiver um Service zu starten wenn Gerät gebootet wurde
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent myIntent = new Intent(context, BackgroundService.class);
            context.startService(myIntent);
        }
    }
}
