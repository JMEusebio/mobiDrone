package asu.cse535.group3.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Calendar;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap mGoogleMap;
    boolean initialpass;
    boolean schedule = false;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LatLng currlatLng;
    Marker mDLocationMarkerMarker;
    LatLng[] favlocs;
    private DatabaseHandler dh;
    String scheduleHour = "not set";
    String scheduleMinute = "not set";
    private String username = "";
    private String curLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            username = getIntent().getStringExtra("USERNAME");
        }

        initialpass = true;
        favlocs = new LatLng[4];
        favlocs[0] = new LatLng(33.418530,-111.934029);
        favlocs[1] = new LatLng(33.415517,-111.927546);
        favlocs[2] = new LatLng(33.420117,-111.932136);
        favlocs[3] = new LatLng(33.423567,-111.939269);

        if (username.equals("")) {
            Log.d("oh", "oh");
            Intent intent = new Intent(MapPickerActivity.this, LoginActivity.class);
            MapPickerActivity.this.startActivity(intent);


            super.onCreate(savedInstanceState);
        }
        else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map_picker);

            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);

            dh = new DatabaseHandler(this);

            getSupportActionBar().setTitle("Select Location");

            Button radio1 = (Button) findViewById(R.id.radioButton);
            radio1.setOnClickListener(new android.view.View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    schedule = false;

                    // DroneServer.RequestDrone(currlatLng.latitude,currlatLng.longitude,mDLocationMarkerMarker.getPosition().latitude,mDLocationMarkerMarker.getPosition().longitude);

                }
            });

            //Spinner spinner = (Spinner) findViewById(R.id.spinner);
            //spinner.setOnItemSelectedListener(this);

            Button radio2 = (Button) findViewById(R.id.radioButton2);
            radio2.setOnClickListener(new android.view.View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    schedule = true;

                    // DroneServer.RequestDrone(currlatLng.latitude,currlatLng.longitude,mDLocationMarkerMarker.getPosition().latitude,mDLocationMarkerMarker.getPosition().longitude);

                }
            });

            Button reqButton = (Button) findViewById(R.id.requestButton);
            reqButton.setOnClickListener(new android.view.View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    if (!schedule) {

                        Intent intent = new Intent(getBaseContext(), DroneMapActivity.class);

                        DroneServer.RequestDrone(currlatLng.latitude, currlatLng.longitude, mDLocationMarkerMarker.getPosition().latitude, mDLocationMarkerMarker.getPosition().longitude);

                        intent.putExtra("dlat", mDLocationMarkerMarker.getPosition().latitude);
                        intent.putExtra("dlong", mDLocationMarkerMarker.getPosition().longitude);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //startActivityForResult(intent,0);
                        startActivity(intent);

                    }

                    if (schedule) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapPickerActivity.this);
                        builder.setMessage("Thank you for scheduling a drone. It will be dispatched at your selected time")
                                .setPositiveButton("Close", null)
                                .create()
                                .show();

                        //Send request to server to send drone at future time scheduleTime
                        String scheduleTime = scheduleHour + scheduleMinute;
                        DroneServer.ScheduleDrone(currlatLng.latitude, currlatLng.longitude, mDLocationMarkerMarker.getPosition().latitude, mDLocationMarkerMarker.getPosition().longitude, scheduleTime);

                       // Calendar c = Calendar.getInstance();
                       // int hour = c.get(Calendar.HOUR_OF_DAY);
                       // int minute = c.get(Calendar.MINUTE);

//                if(hour == Integer.parseInt(scheduleHour) && minute == Integer.parseInt(scheduleMinute)) {


                        //
                        //            }
                    }

                    if (curLocation.equals("Current Location"))
                        dh.addLocation(username, "Lat: " + currlatLng.latitude + "/Long: " + currlatLng.longitude);
                    else
                        dh.addLocation(username, curLocation);

                }
            });

            Spinner spinner3 = (Spinner) findViewById(R.id.spinner);
            spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    scheduleHour = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            Spinner spinner4 = (Spinner) findViewById(R.id.spinner);
            spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    scheduleMinute = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            Spinner faves = (Spinner) findViewById(R.id.spinner);
            faves.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    // NEXT 2 LINES NEED TO BE MOVED. NEED TO EXECUTE WHEN 'REQUEST DRONE' BUTTON IS PRESSED
                    curLocation = parent.getItemAtPosition(position).toString();



                    if (!initialpass) {
                        if (mDLocationMarkerMarker != null) {
                            mDLocationMarkerMarker.remove();
                        }
                        MarkerOptions markerOptions = new MarkerOptions();
                        if (position == 0) {
                            markerOptions.position(currlatLng);
                        } else {
                            markerOptions.position(favlocs[position - 1]);
                        }
                        markerOptions.draggable(true);
                        markerOptions.title("Current Position");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mDLocationMarkerMarker = mGoogleMap.addMarker(markerOptions);
                        mDLocationMarkerMarker.setDraggable(true);

                        TextView dloc = (TextView) findViewById(R.id.textView);
                        dloc.setText("Destination: " + favlocs[position].latitude + ", " + favlocs[position].longitude);
                        Button reqButton = (Button) findViewById(R.id.requestButton);
                        reqButton.setEnabled(true);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                return;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                TextView dloc = (TextView) findViewById(R.id.textView);
                Location l1 = new Location("");
                l1.setLatitude(arg0.getPosition().latitude);
                l1.setLongitude(arg0.getPosition().longitude);

                Location l2 = new Location("");
                l2.setLatitude(currlatLng.latitude);
                l2.setLongitude(currlatLng.longitude);
                if (l1.distanceTo(l2) > 15){
                    dloc.setText("Destination: " + arg0.getPosition().latitude + ", " + arg0.getPosition().longitude);
                    Button reqButton = (Button) findViewById(R.id.requestButton);
                    reqButton.setEnabled(true);
                }
                else{
                    dloc.setText("Destination: Location is too close to current Location");
                    Button reqButton = (Button) findViewById(R.id.requestButton);
                    reqButton.setEnabled(false);
                }


            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                return;
            }
        });


        //Initialize Google Play Services
        // following taken from http://stackoverflow.com/questions/34582370/how-can-i-show-current-location-on-a-google-map-on-android-marshmallow with some slight modification

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    // following 4 methods taken from http://stackoverflow.com/questions/34582370/how-can-i-show-current-location-on-a-google-map-on-android-marshmallow with some slight modification

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        currlatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (initialpass==true){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            initialpass = false;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.draggable(true);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mDLocationMarkerMarker = mGoogleMap.addMarker(markerOptions);
            mDLocationMarkerMarker.setDraggable(true);
        }

    }

    // lines 304-371 taken from http://stackoverflow.com/questions/42637410/how-to-get-current-location-within-a-google-maps-fragment-in-a-viewpager-with-ta
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

              new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapPickerActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }






}
