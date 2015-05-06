package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


/**
 * Created by Stefan on 21.04.2015.
 */
public class MyMessagesFragment extends ListFragment {

    private final String myMessagesExtension = ".txt";
    DownloadTask performBackgroundTask;
    private ArrayList<Topics> subscribedTopics = new ArrayList<>();
    private String myMessagesPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_mymessages, container, false);

        performBackgroundTask = new DownloadTask(getActivity());

        myMessagesPath = getActivity().getFilesDir() + "/mymessages/";

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllSubscribedTopics();
        pullAllNewMessages(); //Ladet alle neuen Messages der Registrierten Topics vom Server und speichert diese in die Datei.
        refreshListView();
    }

    /**
     * Aktualisiert die Liste mit Broadcasts die aktuell geladen sind.
     */
    private void refreshListView() {

        String[] values = new String[subscribedTopics.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = subscribedTopics.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Topics topic = subscribedTopics.get(position);

        Intent intent = new Intent(getActivity(), MessageListActivity.class);
        intent.putExtra("title", topic.getName());
        intent.putExtra("key", topic.getIdentifier());
        intent.putStringArrayListExtra("messages", topic.getMessages());

        startActivity(intent);
    }

    /**
     * Holt alle Messages vom Server die aboniert und neuer als der letzte Timestamp sind.
     */
    private void pullAllNewMessages() {

        for (int i = 0; i < subscribedTopics.size(); i++) {

            Topics topic = subscribedTopics.get(i); //Topic zum prüfen.
            long timestamp = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("timestamp", 1);

            try {
                String[] newMessages = pullNewMessagesByKey(topic.getIdentifier(), timestamp);

                if (newMessages.length > 0) {
                    for (String message : newMessages) {
                        topic.addMessage(message);
                    }
                }
            } catch (JSONException e) {
                Log.e("pullNewMessages:", "Fehler beim pullen vom Server.");
            } catch (Exception ex) {
                Log.e("Sömthing went wröng", ex.getMessage());
            }

            //Speichere Topic mit neuen Nacrichten.
            TopicPersistor.persistTopic(topic, myMessagesPath);
        }
        //Setze neuen Timestamp (zuletzt Aktualisiert)
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putLong("timestamp", Calendar.getInstance().getTimeInMillis() / 1000);
        editor.apply();
    }

    /**
     * Holt alle Messages zu einem topic(Identifier), die neuer sind als der Timestamp.
     *
     * @param identifier Identifier des Topics.
     * @param timestamp  Nachrichten die neuer sind als diese werden geladen.
     * @return Meesages zum Identifier die neuer sind als der Timestamp.
     * @throws Exception JSONException Falls die Messages nicht geparst werden können.
     */
    private String[] pullNewMessagesByKey(String identifier, long timestamp) throws Exception {

        try {
            //Pulle nachrichten vom Server mit Identifier und Timestamp
            String result = null;
            performBackgroundTask = new DownloadTask(getActivity());
            result = performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://mikegernet.ch/mobpro/index.php?get=" + identifier + "&timestamp=" + timestamp).get();

            //Wenn keine neuen Nachrichten liefere new String[0] zurück.
            if (result == null) {
                return new String[0];
            }

            //Parse den String nach JSON Objekten und extrahiere Nachrichten
            String[] messages = parseJSONforMessages(result);
            return messages;

        } catch (InterruptedException e) {
            Log.e("Pull Messages", "pull failed.");
            throw new JSONException("Could not Parse JSON");
        } catch (ExecutionException e) {
            Log.e("Pull Messages", "pull failed.");
            throw new JSONException("Could not Parse JSON");
        } catch (Exception ex) {
            performBackgroundTask.getStatus();
            Log.e("Öppis mitm executor", "komisch");
            throw new Exception(performBackgroundTask.getStatus().toString());
        }
    }


    /**
     * Parst einen String nach (im JSON-Format) nach messages.
     *
     * @param json String im JSON-Format zum parsen.
     * @return Geparste Messages. leeres Array falls keine neuen Messages.
     * @throws JSONException Parser error.
     */
    public String[] parseJSONforMessages(String json) throws JSONException {
        try {
            JSONArray jsonArray = new JSONArray(json);
            String[] messages = new String[jsonArray.length()];
            if (messages == null) {
                return new String[0];
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String timestamp = jsonObject.getString("timestamp");
                String message = jsonObject.getString("message");
                messages[i] = message;
            }
            return messages;
        } catch (JSONException e) {
            Log.e("JSON-Parser", "Could not parse JSON");
            throw new JSONException(e.getMessage());
        }
    }

    /**
     * Lädt alle Dateien im Filepfad für "mymessages" in die Memebervariable subscribedTopics.
     */
    private void loadAllSubscribedTopics() {
        subscribedTopics = new ArrayList<>();

        File files = new File(myMessagesPath);
        if (files != null) {
            for (File f : files.listFiles()) {
                Topics topic = TopicPersistor.loadTopicFromFile(f);
                subscribedTopics.add(topic);
            }
        } else {
            Log.i("Load Subscribed Topics", "No Topics Subscribed");
        }
    }
}
