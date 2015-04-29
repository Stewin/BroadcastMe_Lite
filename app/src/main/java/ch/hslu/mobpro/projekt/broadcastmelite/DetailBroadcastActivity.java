package ch.hslu.mobpro.projekt.broadcastmelite;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Stefan on 29.04.2015.
 */
public class DetailBroadcastActivity extends Activity {

    private TextView tvTopic;
    private TextView tvInfos;
    private Button btnSendMessage;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailbroadcast);

        tvTopic = (TextView) findViewById(R.id.tvBroadcastTitle);
        tvInfos = (TextView) findViewById(R.id.tvInfos);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
    }

    public void OnSendMessageClicked(View v) {
        Log.i("Detail BC Activity: ", "Send Message click");
        //ToDo: Send Message to Server.
    }

}
