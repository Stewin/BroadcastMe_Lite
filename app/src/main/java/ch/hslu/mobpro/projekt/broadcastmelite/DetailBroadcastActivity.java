package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private Button btnSendMessage;
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
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);

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
    protected void onStop() {
        super.onStop();
        persistCurrentBroadcast();
    }

    //Speichert den Broadcast der aktuell auf der Activity angezeigt wird.
    private void persistCurrentBroadcast() {
        Gson gson = new Gson();
        Topics currentBroadcast = new Topics(key, topicTitle);
        for (String s : messages) {
            currentBroadcast.addMessage(s);
        }

        String json = gson.toJson(currentBroadcast);

        String text = json;
        File directoryPath = new File(myBroadcastsPath);
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
        }

        String filename = key + myBroadcastExtension;
        File outfile = new File(directoryPath, filename);

        if (outfile.exists()) {
            outfile.delete();
        }

        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(outfile);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(text);
            bufferedWriter.flush();
        } catch (IOException ex) {
            Log.e("Persistenz", "Error beim schreiben");
            System.out.println(ex.toString());
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ioex) {
                Log.e("saveTopicsInFiles", "Could not close BufferedWriter");
            }
        }
    }

    /**
     * OnClickListener dür den Send Message Button.
     *
     * @param v View des Buttons.
     */
    public void OnSendMessageClicked(View v) {
        Log.i("Detail BC Activity: ", "Send Message click");

        String message = tvMessage.getText().toString();

        try {
            performBackgroundTask.execute("http://mikegernet.ch/mobpro/index.php?post=" + key + "&message=" + message).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e("Send Message: ", "Message konnte nicht an Server gesendet werden.");
        }
        messages.add(message);
    }


    /**
     * Setzt die Membervariable für den Titel neu falls dieser geändert wurde.
     */
    private class TitleChangedWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.i("beforeTextChanged", "before Title changed");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i("onTextChanged", "on Title changed");
            topicTitle = tvTopic.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.i("afterTextChanged", "Title changed");

        }
    }

}
