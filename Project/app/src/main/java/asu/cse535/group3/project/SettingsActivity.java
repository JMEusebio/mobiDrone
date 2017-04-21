package asu.cse535.group3.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);



        Button fButton = (Button) findViewById(R.id.settingsFeedback);
        fButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent intent = new Intent(SettingsActivity.this, MessageCenter.class);
                startActivity(intent);
            }

        });

        Button history = (Button) findViewById(R.id.locationHistory);
        history.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("Please view the request page for location history")
                        .setPositiveButton("Continue",null)
                        .create()
                        .show();


            }

        });

        Button userDev = (Button) findViewById(R.id.userDev);
        userDev.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("You have deviated from the path. Please return and press Continue.")
                        .setPositiveButton("Continue",null)
                        .setNegativeButton("End Trip", null)
                        .create()
                        .show();
            }

        });

        Button registerNew = (Button) findViewById(R.id.registerNew);
        registerNew.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
            //    Intent intent = new Intent(SettingsActivity.this, RegisterActivity.class);
             //   startActivity(intent);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("You have reached your final destination. Thank you for using MobiDrone")
                        .setPositiveButton("End Trip",null)

                        .create()
                        .show();


            }

        });



        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                finish();
            }});
    }
}
