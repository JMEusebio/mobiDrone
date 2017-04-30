package asu.cse535.group3.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.widget.TextView;


public class HistoryActivity extends AppCompatActivity {

    private String username = "";
    private DatabaseHandler dh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_history);

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            username = getIntent().getStringExtra("USERNAME");
        }

        dh = new DatabaseHandler(this);

        String locations = dh.getLocations(username);

        Log.d("locations", locations);
        String[] locationsArray = locations.split(",");


        TextView history1 = (TextView) findViewById(R.id.history1);
        TextView history2 = (TextView) findViewById(R.id.history2);
        TextView history3 = (TextView) findViewById(R.id.history3);
        TextView history4 = (TextView) findViewById(R.id.history4);
        TextView history5 = (TextView) findViewById(R.id.history5);


        history1.setText(locationsArray[4]);
        history2.setText(locationsArray[3]);
        history3.setText(locationsArray[2]);
        history4.setText(locationsArray[1]);
        history5.setText(locationsArray[0]);

    }
}
