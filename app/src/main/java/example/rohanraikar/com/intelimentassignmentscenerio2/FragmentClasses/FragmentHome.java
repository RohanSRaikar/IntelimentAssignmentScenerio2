package example.rohanraikar.com.intelimentassignmentscenerio2.FragmentClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import example.rohanraikar.com.intelimentassignmentscenerio2.BackgroundServices.DataHandler;
import example.rohanraikar.com.intelimentassignmentscenerio2.BackgroundServices.JSONParser;
import example.rohanraikar.com.intelimentassignmentscenerio2.BackgroundServices.LocationAddress;
import example.rohanraikar.com.intelimentassignmentscenerio2.BackgroundServices.LocationServiceClass;
import example.rohanraikar.com.intelimentassignmentscenerio2.MainActivity;
import example.rohanraikar.com.intelimentassignmentscenerio2.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by Rohan.Raikar on 31/03/2017.
 */

public class FragmentHome extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 200;
    LocationServiceClass myService;
    TextView tvMyAddress,tvCarTransport,tvTrainTransport;
    JSONObject jsonobject;
    Button startNavigation;
    ImageButton btnGetLocation;
    DataHandler dh;
    String msg=null;
    Bundle bundle;
    JSONArray jArray;
    ArrayList<String> names;
    View v;
    Context context;
    ArrayList<DataHandler> serverData;
    Spinner loadData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_activity,container,false);
        bundle=new Bundle();
        context=getActivity();
        setRetainInstance(true);
        //Download data from the server
        new DownloadJSON().execute();
        names=new ArrayList<String>();
        //Check for location permissions grants
        if(checkPermissions()){
            //if permissions are granted do nothing
        }
        else
        {
            //if permissions are not granted ask the user for the permissions.
            requestPermission();
        }
        startNavigation=(Button)v.findViewById(R.id.btn_Navigate);
        myService = new LocationServiceClass(getActivity());
        tvMyAddress=(TextView)v.findViewById(R.id.TV_currAddress);
        tvCarTransport=(TextView)v.findViewById(R.id.TV_carTransport);
        tvTrainTransport=(TextView)v.findViewById(R.id.TV_trainTransport);
        btnGetLocation=(ImageButton)v.findViewById(R.id.btn_locateMe);
        loadData=(Spinner)v.findViewById(R.id.SP_names);
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }
        });
        startNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigate to the map activity
                Fragment mapFragment = new FragmentMap();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentHolder, mapFragment);
                mapFragment.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        return v;
    }


    private void getMyLocation() {
        Location location = myService.getLocation();
        if (location != null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,getActivity(), new GeocoderHandler());

        }
    }


    //Function returns true if the permission is already granted if not returns false
    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    //function asks for the permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted)
                        Toast.makeText(getActivity(), "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_LONG).show();
                    else {

                        Toast.makeText(getActivity(), "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to location permissions for using this app",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
        }
    }//end of onRequestPermissionsResult

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tvMyAddress.setText(locationAddress);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private class DownloadJSON extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            names=new ArrayList<String>();
            serverData=new ArrayList<DataHandler>();
            jArray = JSONParser.getJSONfromURL("http://express-it.optusnet.com.au/sample.json");
            Log.d("Rohan","Length Received Json object" + jArray.length());
                try {
                    for(int i=0;i<=jArray.length();i++) {
                        jsonobject = jArray.getJSONObject(i);
                        //Storing name into a class object
                        Log.d("Rohan","Json names "+i+","+jsonobject.getString("name"));
                        names.add(jsonobject.getString("name"));
                        dh=new DataHandler(jsonobject.getInt("id"),jsonobject.getString("name"),jsonobject.getJSONObject("fromcentral"),jsonobject.getJSONObject("location"));
                        serverData.add(dh);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                loadData=(Spinner)v.findViewById(R.id.SP_names);
            Log.d("Rohan","value of name : "+names);
            Log.d("Rohan","value of spinner :"+loadData );
            Log.d("Rohan","Value of activity :"+context);

                loadData.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,names));
                loadData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        JSONObject trasportDetails=serverData.get(position).getTranportationDetails();
                        JSONObject location=serverData.get(position).getLocations();
                        //Getting data to pass to the Map activity
                        try {
                            bundle.putString("latitude",location.getString("latitude").toString());
                            bundle.putString("longitude",location.getString("longitude").toString());
                            bundle.putString("carTransport",trasportDetails.getString("car").toString());
                            bundle.putString("trainTransport",trasportDetails.getString("train").toString());
                            bundle.putString("cityName",serverData.get(position).getName());
                            Log.d("Rohan","Location :"+location.getString("latitude").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (trasportDetails.has("car")) {
                                tvCarTransport.setText("Car -"+trasportDetails.getString("car").toString());
                            }
                            else{
                                tvCarTransport.setText("Car - NA");
                            }

                            if (trasportDetails.has("train")) {
                                tvTrainTransport.setText("Train -"+trasportDetails.getString("train").toString());
                            }
                            else
                            {
                                tvTrainTransport.setText("Train - NA");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                });
        }
    }


}
