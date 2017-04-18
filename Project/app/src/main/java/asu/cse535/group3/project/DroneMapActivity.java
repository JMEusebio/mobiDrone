package asu.cse535.group3.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import asu.cse535.group3.project.DroneServer;

import static java.lang.Double.parseDouble;

public class DroneMapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    Double dlatitude;
    Double dlongitude;
    private GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    boolean initialpass;
    boolean viewing;
    boolean lights;
    boolean alertstart;
    Marker mDestinationMarker;
    LocationRequest mLocationRequest;
    Location destloc;
    LatLng currlatLng;
    boolean dronepresent;
    LatLng dronelatLng;
    Marker mDroneMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dronemap_activity);

        Bundle ibundle = getIntent().getExtras();
        initialpass = true;
        viewing = false;
        lights = false;
        alertstart = false;
        dlatitude = ibundle.getDouble("dlat");
        dlongitude = ibundle.getDouble("dlong");
        destloc = new Location("");
        destloc.setLatitude(dlatitude);
        destloc.setLongitude(dlongitude);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        getSupportActionBar().setTitle("Navigation");

        Button setButton = (Button) findViewById(R.id.settingsButton);
        setButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){


                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);

            }});

        final ImageView movieim = (ImageView) findViewById(R.id.movieviewing);
        final TextView movietext = (TextView) findViewById(R.id.textviewing);
        final Button viewingButton = (Button) findViewById(R.id.videoButton);
        viewingButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                if (viewing){
                    viewing = false;
                    viewingButton.setText("Start Video");
                    movieim.setVisibility(View.INVISIBLE);

                    movieim.getLayoutParams().height = 1;
                    movieim.requestLayout();
                    movietext.setVisibility(View.INVISIBLE);

                }
                else{
                    viewing = true;
                    viewingButton.setText("Stop Video");
                    movieim.setVisibility(View.VISIBLE);

                    movieim.getLayoutParams().height = 500;
                    movieim.requestLayout();
                    movietext.setVisibility(View.VISIBLE);

                }
            }});

        final Button lightButton = (Button) findViewById(R.id.lightButton);
        lightButton.setBackgroundColor(Color.DKGRAY);
        lightButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                if (lights){
                    lights = false;
                    lightButton.setBackgroundColor(Color.DKGRAY);
                    DroneServer.EnableLight(false);

                }
                else{
                    lights = true;
                    lightButton.setBackgroundColor(Color.YELLOW);
                    DroneServer.EnableLight(true);

                }
            }});

        final Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v){
                setResult(RESULT_OK);
                finish();
            }});

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

        /*
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                return;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                return;
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                return;
            }
        });
        */

        //Initialize Google Play Services
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
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
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


        checkdistance(location, destloc);



        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlatLng,mGoogleMap.getCameraPosition().zoom));
        LatLng dll = new LatLng(dlatitude,dlongitude);
        dronepresent = true;
        if (dronepresent){
            if (mDroneMarker != null) {
                mDroneMarker.remove();
            }
            dronelatLng = new LatLng(location.getLatitude()+.001,location.getLongitude());
            MarkerOptions dmarkerOptions = new MarkerOptions();
            dmarkerOptions.position(dronelatLng);
            dmarkerOptions.title("Drone");
            if (lights) {
                dmarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
            else{
                dmarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            }
            mDestinationMarker = mGoogleMap.addMarker(dmarkerOptions);
        }




        if (initialpass==true) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currlatLng, 13));
            initialpass = false;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(dll);
            markerOptions.title("Destination");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mDestinationMarker = mGoogleMap.addMarker(markerOptions);


            //directions:


            String toparse = DroneServer.GetBestPath(currlatLng.latitude, currlatLng.longitude);
            String[] parts = toparse.split("Start at \\(");
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            for (int a = 1; a < parts.length; a++) {
                parts[a] = parts[a].split("\\), E")[0];
                Double lat = parseDouble(parts[a].split(",")[0]);
                Double lng = parseDouble(parts[a].split(",")[1]);
                LatLng temp = new LatLng(lat, lng);
                points.add(temp);

            }
            PolylineOptions lineOptions = new PolylineOptions();
            if (points.size() > 1)
                {
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.BLUE);
                    mGoogleMap.addPolyline(lineOptions);
                }

            }
            String eta = DroneServer.GetEta(currlatLng.latitude,currlatLng.longitude);
            TextView etaTextView = (TextView) findViewById(R.id.etaText);
            etaTextView.setText("ETA: " + eta.replace("\"",""));



    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(DroneMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
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

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkdistance(Location start, Location end){
        float x = 0f;
        x = start.distanceTo(end);
        if (x<70 && (alertstart == false)) {
            alertstart = true;
            new AlertDialog.Builder(this)
                    .setTitle("Navigation")
                    .setMessage("You have reached your location")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
    }

}
