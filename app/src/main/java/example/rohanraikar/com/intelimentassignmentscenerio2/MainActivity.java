package example.rohanraikar.com.intelimentassignmentscenerio2;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import example.rohanraikar.com.intelimentassignmentscenerio2.FragmentClasses.FragmentHome;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkAvailable()) {
            // Create an Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required")
                    .setCancelable(false)
                    .setPositiveButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // Restart the Activity
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            
                    // Recheck Network Connection
                    if (!isNetworkAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this);
                        builder.setMessage("Internet Connection Required")
                                .setCancelable(false)
                                .setPositiveButton("Retry",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // Restart the activity
                                                Intent intent = new Intent(
                                                        MainActivity.this,
                                                        MainActivity.class);
                                                finish();
                                                startActivity(intent);

                                            }

                                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {

                        loadFragment();
                    }
                }

    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void loadFragment() {
        Fragment fragment=new FragmentHome();
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        transaction.replace(R.id.fragmentHolder,fragment);
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadFragment();
    }


}
