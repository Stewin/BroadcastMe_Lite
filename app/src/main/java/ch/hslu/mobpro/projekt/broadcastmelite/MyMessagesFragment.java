package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        this.loadAllSubscribedTopics();

        refreshListView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        pullAllNewMessages();
        refreshListView();
    }

    /**
     * Aktualisiert die Liste mit Broadcasts.
     */
    private void refreshListView() {
        this.loadAllSubscribedTopics();

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
            Topics topic = subscribedTopics.get(i);
            topic.getMessages().clear();
            try {
                String[] newMessages = pullNewMessagesByKey(topic.getIdentifier());

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
            persistTopics(topic);
        }
    }

    /**
     * Holt alle Messages zu einem topic(Identifier), die neuer sind als der Timestamp.
     *
     * @param identifier Identifier des Topics.
     * @return Meesages zum Identifier die neuer sind als der Topic.
     * @throws JSONException Falls die Messages nicht geparst werden können.
     */
    private String[] pullNewMessagesByKey(String identifier) throws Exception {
        try {
            long timestamp = 1;
            //ToDo: Add Timestamp from preferences to the Request instead of 1.

            String result = null;

            performBackgroundTask = new DownloadTask(getActivity());
            result = performBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://mikegernet.ch/mobpro/index.php?get=" + identifier + "&timestamp=" + timestamp).get();


            if (result == null) {
                return new String[0];
            }
            return parseJSONforMessages(result);
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
            int numberOfMessages = jsonArray.length();
            String[] messages = new String[numberOfMessages];
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
        subscribedTopics.clear();
        File files = new File(myMessagesPath);
        if (files != null) {
            for (File f : files.listFiles()) {
                Topics topic = parseTopicFromFile(f);
                subscribedTopics.add(topic);
            }
        } else {
            Log.i("Load Subscribed Topics", "No Topics Subscriped");
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

    //Speichert den Broadcast der aktuell auf der Activity angezeigt wird.
    private void persistTopics(Topics topic) {
        Gson gson = new Gson();


        String json = gson.toJson(topic);

        String text = json;
        File directoryPath = new File(myMessagesPath);
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
        }

        String filename = topic.getIdentifier() + myMessagesExtension;
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
}
