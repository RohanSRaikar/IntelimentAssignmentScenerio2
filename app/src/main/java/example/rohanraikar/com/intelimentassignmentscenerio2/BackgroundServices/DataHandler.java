package example.rohanraikar.com.intelimentassignmentscenerio2.BackgroundServices;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Rohan.Raikar on 31/03/2017.
 */

public class DataHandler {
    JSONObject transport;
    JSONObject location;
    String name;
    int id;

    public DataHandler(int id, String name, JSONObject fromcentral, JSONObject location) {
        this.id = id;
        this.name = name;
        this.transport=fromcentral;
        this.location=location;
    }

    public String getName(){return name;}
    public int getId(){return id;}
    public JSONObject getTranportationDetails(){return transport;}
    public JSONObject getLocations(){return location;};
}
