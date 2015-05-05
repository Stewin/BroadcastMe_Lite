package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefan on 29.04.2015.
 */
public class MessageListActivity extends ListActivity {

    private final String myTopicsExtension = ".txt";
    private ListView lvMessageList;
    private TextView etTopicTitle;
    private String topicTitle;
    private String key;
    private ArrayList<String> messages = new ArrayList<>();
    private String myMessagesPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messagelist);
        myMessagesPath = getFilesDir() + "/mymessages/";
        ;

        etTopicTitle = (TextView) findViewById(R.id.etTopicTitle);
        etTopicTitle.addTextChangedListener(new TextChangedWatcher());

        topicTitle = getIntent().getStringExtra("title");
        etTopicTitle.setText(topicTitle);

        key = getIntent().getStringExtra("key");

        ArrayList<String> messages = getIntent().getStringArrayListExtra("messages");

        String[] values = new String[messages.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = messages.get(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    /**
     * Speichert den Broadcast beim Schliessen der Activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        persistCurrentTopic();
    }

    //Speichert den Broadcast der aktuell auf der Activity angezeigt wird.
    private void persistCurrentTopic() {
        Gson gson = new Gson();
        Topics currentTopic = new Topics(key, topicTitle);
        for (String s : messages) {
            currentTopic.addMessage(s);
        }

        String json = gson.toJson(currentTopic);

        String text = json;
        File directoryPath = new File(myMessagesPath);
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
        }

        String filename = key + myTopicsExtension;
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

    private class TextChangedWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            topicTitle = etTopicTitle.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
