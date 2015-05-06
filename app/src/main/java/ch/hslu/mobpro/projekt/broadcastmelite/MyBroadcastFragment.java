package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Stefan on 21.04.2015.
 */
public class MyBroadcastFragment extends ListFragment {

    private final String myBroadcastExtension = ".txt";
    private ArrayList<Topics> myOwnBroadcasts = new ArrayList<>();
    private String myBroadcastsPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_mybroadcasts, container, false);

        myBroadcastsPath = getActivity().getFilesDir() + "/mybroadcasts/";

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllOwnBroadcasts();
        refreshListView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(this.getListView());
    }

    /**
     * Aktualisiert die Liste (View) mit den zurzeit geladenen Broadcasts.
     */
    private void refreshListView() {

        String[] values = new String[myOwnBroadcasts.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = myOwnBroadcasts.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }


    /**
     * Lädt alle Dateien im Filepfad für "mybroadcasts" in die Memebervariable myOwnBroadcasts.
     */
    private void loadAllOwnBroadcasts() {
        myOwnBroadcasts = new ArrayList<>();

        File files = new File(myBroadcastsPath);
        if (files != null) {
            for (File f : files.listFiles()) {
                Topics topic = TopicPersistor.loadTopicFromFile(f);
                myOwnBroadcasts.add(topic);
            }
        } else {
            Log.i("Load Own Broadcasts", "No Broadcasts available");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), DetailBroadcastActivity.class);
        Topics topic = myOwnBroadcasts.get(position);
        intent.putExtra("title", topic.getName());
        intent.putExtra("key", topic.getIdentifier());

        String[] messageArray = new String[topic.getMessages().size()];
        intent.putExtra("messages", topic.getMessages().toArray(messageArray));

        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = this.getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextmenu_broadcastme, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case R.id.action_delete:
                Topics broadcastToDelete = myOwnBroadcasts.get((int) info.id);
                TopicPersistor.deleteTopic(broadcastToDelete, myBroadcastsPath);
                myOwnBroadcasts.remove(broadcastToDelete);
                refreshListView();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
