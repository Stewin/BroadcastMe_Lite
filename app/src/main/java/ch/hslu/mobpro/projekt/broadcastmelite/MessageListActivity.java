package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Stefan on 29.04.2015.
 */
public class MessageListActivity extends Activity {

    private ListView messageList;
    private TextView topicTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messagelist);

        topicTitle = (TextView) findViewById(R.id.tvTopic);

        //ToDo: Fill Content of List with Messages from one Topic
    }
}
