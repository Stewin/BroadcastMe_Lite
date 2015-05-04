package ch.hslu.mobpro.projekt.broadcastmelite;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefan on 21.04.2015.
 */
public class MyBroadcastFragment extends ListFragment {

    private final String myBroadcastExtension = ".txt";
    private ListView listView;
    private ArrayList<Topics> myOwnBroadcasts = new ArrayList<>();
    private String myBroadcastsPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_mybroadcasts, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_item);

        myBroadcastsPath = getActivity().getFilesDir() + "/mybroadcasts/";
        this.loadAllOwnBroadcasts();

        String[] values = new String[myOwnBroadcasts.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = myOwnBroadcasts.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

        return rootView;
    }

    /**
     * Lädt alle Dateien im Filepfad für "mybroadcasts" in die Memebervariable myOwnBroadcasts.
     */
    private void loadAllOwnBroadcasts() {
        File files = new File(myBroadcastsPath);
        for (File f : files.listFiles()) {
            Topics topic = parseTopicFromFile(f);
            myOwnBroadcasts.add(topic);
        }
    }

    /**
     * Parst ein Topics-Objekt aus dem File.
     *
     * @param file File zum parsen.
     * @return Topics-Objekt.
     */
    private Topics parseTopicFromFile(File file) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String text = "";
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) text += tmp;
            fileReader.close();
            bufferedReader.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("IO Exception");
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                Log.e("parseTopicFromLine", "Could not close BufferedReader!");
            }
        }
        Gson gson = new Gson();
        return gson.fromJson(text, Topics.class);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //ToDo: Implement Logic for a Click on a BroadcastItem
    }
}
