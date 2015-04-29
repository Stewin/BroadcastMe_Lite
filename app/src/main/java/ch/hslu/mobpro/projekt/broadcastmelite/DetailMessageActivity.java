package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Stefan on 29.04.2015.
 */
public class DetailMessageActivity extends Activity {

    private TextView topicTitle;
    private TextView messageTitle;
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailmessage);

        topicTitle = (TextView) findViewById(R.id.tvTopicTitle);
        messageTitle = (TextView) findViewById(R.id.tvTextTitle);
        content = (TextView) findViewById(R.id.tvContent);

        topicTitle.setText(getIntent().getStringExtra("topic"));
        messageTitle.setText(getIntent().getStringExtra("messagetitle"));
        content.setText(getIntent().getStringExtra("content"));
    }
}
