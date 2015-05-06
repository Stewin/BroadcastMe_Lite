package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        etTopicTitle = (TextView) findViewById(R.id.etTopicTitle);
        etTopicTitle.addTextChangedListener(new TextChangedWatcher());

        topicTitle = getIntent().getStringExtra("title");
        etTopicTitle.setText(topicTitle);

        key = getIntent().getStringExtra("key");

        messages = getIntent().getStringArrayListExtra("messages");

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

        Topics currentTopic = new Topics(key, topicTitle);
        for (String s : messages) {
            currentTopic.addMessage(s);
        }

        TopicPersistor.persistTopic(currentTopic, myMessagesPath);
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
