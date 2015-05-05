package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Stefan on 29.04.2015.
 */
public class MessageListActivity extends ListActivity {

    private ListView lvMessageList;
    private TextView tvTopicTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messagelist);

        tvTopicTitle = (TextView) findViewById(R.id.tvTopic);

        tvTopicTitle.setText(getIntent().getStringExtra("title"));

        ArrayList<String> messages = getIntent().getStringArrayListExtra("messages");

        String[] values = new String[messages.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = messages.get(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }
}
