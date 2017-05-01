package asu.cse535.group3.project;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;

import static android.support.v7.appcompat.R.styleable.View;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button navButton = (Button) findViewById(R.id.NavigationButton);

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            username = getIntent().getStringExtra("USERNAME");
        }

        navButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MapPickerActivity.class);
                intent.putExtra("USERNAME", username);
                //finish();
                //finish();
                startActivity(intent);

            }

        });

        Button call = (Button) findViewById(R.id.button3);
        call.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:4809653456"));
                startActivity(intent);
            }

        });

        Button login = (Button) findViewById(R.id.button2);
        login.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginIntent.putExtra("USERNAME", username);
                Log.d("user", username);
                startActivity(loginIntent);
            }

        });

        Button panicButton = (Button)findViewById(R.id.PanicButton);
        panicButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v){
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // This sample is simply for testing the service, it is not the actual
                        // functionality of the panic button.
                        // Brickyard to Barrett example.
                        DroneServer.RequestDrone(33.4231196, -111.9420774, 33.4148218, -111.929581);
                        System.out.println(DroneServer.GetBestPath());
                    }
                });
                t.run();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("A drone has been dispatched to your location. Please stay calm. The police have been contacted.")
                        .setPositiveButton("Continue",null)

                        .create()
                        .show();


            }
        });

        // This code allows us to make service calls from the main thread, or threads spawned there.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
