package ch.hslu.mobpro.projekt.broadcastmelite;

import java.util.ArrayList;

/**
 * Created by mike on 04.05.2015.
 */
public class Topics {

    private String identifier;
    private String name;
    private ArrayList<String> messages;

    public Topics(String identifier, String name){
        this.identifier = identifier;
        this.name = name;
        messages = new ArrayList<>();
    }

    public String getIdentifier(){
        return identifier;
    }

    public String getName(){
        return name;
    }

    public void addMessage(String message){
        messages.add(message);
    }

    public ArrayList<String> getMessages(){
        return messages;
    }

}
