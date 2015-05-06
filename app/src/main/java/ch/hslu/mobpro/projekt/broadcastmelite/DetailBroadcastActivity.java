package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Stefan on 29.04.2015.
 */
public class DetailBroadcastActivity extends Activity {

    private final String myBroadcastExtension = ".txt";
    DownloadTask performBackgroundTask;
    private TextView tvTopic;
    private TextView tvInfos;
    private TextView tvMessage;
    private String topicTitle;
    private String key;
    private ArrayList<String> messages = new ArrayList<>();
    private String myBroadcastsPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailbroadcast);

        tvTopic = (TextView) findViewById(R.id.tvBroadcastTitle);
        tvInfos = (TextView) findViewById(R.id.tvInfos);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        performBackgroundTask = new DownloadTask(this);

        myBroadcastsPath = getFilesDir() + "/mybroadcasts/";

        tvTopic.addTextChangedListener(new TitleChangedWatcher());

        topicTitle = getIntent().getStringExtra("title");
        key = getIntent().getStringExtra("key");

        String[] messagesAsStringArray = getIntent().getStringArrayExtra("messages");
        for (String s : messagesAsStringArray) {
            messages.add(s);
        }

        tvTopic.setText(topicTitle);
        tvInfos.setText("KeyString: " + key);
    }

    /**
     * Speichert den Broadcast beim Schliessen der Activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        persistCurrentBroadcast();
    }

    //Speichert den Broadcast der aktuell auf der Activity angezeigt wird.
    private void persistCurrentBroadcast() {
        Topics currentBroadcast = new Topics(key, topicTitle);
        for (String s : messages) {
            currentBroadcast.addMessage(s);
        }

        TopicPersistor.persistTopic(currentBroadcast, myBroadcastsPath);
    }

    /**
     * OnClickListener für den Send Message Button.
     *
     * @param v View des Buttons.
     */
    public void OnSendMessageClicked(View v) {
        Log.i("Detail BC Activity: ", "Send Message click");

        String message = null;
        try {
            message = URLEncoder.encode(tvMessage.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("URLEncoder: ", "Could not encode message.");
        }

        try {
            performBackgroundTask = new DownloadTask(this);
            performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://mikegernet.ch/mobpro/index.php?post=" + key + "&message=" + message).get();
            messages.add(message);
            tvMessage.setText("");
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e("Send Message: ", "Message konnte nicht an Server gesendet werden.");
        }
    }


    /**
     * Setzt die Membervariable für den Titel neu falls dieser geändert wurde.
     */
    private class TitleChangedWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            topicTitle = tvTopic.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
