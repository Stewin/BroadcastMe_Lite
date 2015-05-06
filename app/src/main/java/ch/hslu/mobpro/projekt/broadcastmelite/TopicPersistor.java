package ch.hslu.mobpro.projekt.broadcastmelite;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Stefan on 06.05.2015.
 */
public class TopicPersistor {

    private static final String FILE_EXTENSION = ".txt";

    /**
     * Persistiert ein Topics-Objet im angegebenen Filpfad. Als Dateiname wird der Identifier des Objekts verwendet.
     *
     * @param topic    Topics-Objekt zum Persistieren.
     * @param fullPath Pfadname (getFilesDir() + ["/mymessages/" | "/mybroadcasts/"] <- ganzen Pfad angeben inkl. getFilesDir()
     */
    public static final void persistTopic(Topics topic, String fullPath) {

        Gson gson = new Gson();

        String json = gson.toJson(topic);

        String text = json;
        File directoryPath = new File(fullPath);

        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
        }

        String filename = topic.getIdentifier() + FILE_EXTENSION;

        File outfile = new File(directoryPath, filename);

        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(outfile, false);
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

    /**
     * Erstellt ein Topics-Objekt aus dem angegebenen File.
     *
     * @param filename File mit einem Enthaltenen Topics-Objekt.
     * @return Topics-Objekt.
     */
    public static final Topics loadTopicFromFile(File filename) {

        FileReader fileReader;
        BufferedReader bufferedReader = null;
        String text = "";
        try {
            fileReader = new FileReader(filename);
            bufferedReader = new BufferedReader(fileReader);
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) text += tmp;
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
}
