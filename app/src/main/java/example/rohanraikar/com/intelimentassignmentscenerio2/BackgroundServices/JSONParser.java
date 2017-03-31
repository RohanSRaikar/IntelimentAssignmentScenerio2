package example.rohanraikar.com.intelimentassignmentscenerio2.BackgroundServices;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Rohan.Raikar on 31/03/2017.
 */

public class JSONParser {

    public static JSONArray getJSONfromURL(String url) {
        InputStream is = null;
        String result = "";
        String line="";
        JSONArray jArray = null;
        //Download JSON data from the URL
        try {
            URL ur=new URL(url);
            URLConnection jc = ur.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            line=convertStreamToString(reader);
            Log.d("Rohan","recevived String:"+line);
            jArray=new JSONArray(line);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jArray;
    }

    private static String convertStreamToString(BufferedReader reader) {

        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
