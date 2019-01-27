package com.univaq.loreand.earthquake.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.univaq.loreand.earthquake.R;
import com.univaq.loreand.earthquake.model.Earthquake;
import com.univaq.loreand.earthquake.utility.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Earthquake> data = new ArrayList<>();

    private Location lastLocation;

    private AdapterRecycler adapter;

    private MyListener listener = new MyListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkConnection()) {

            if(startGPS()){
                downloadData(lastLocation);
            }


        } else {
            Toast.makeText(MainActivity.this, R.string.NO_INTERNET, Toast.LENGTH_SHORT).show();
        }

        adapter = new AdapterRecycler(data);
        RecyclerView list = findViewById(R.id.main_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.main_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        ImageView img = (ImageView) findViewById(R.id.imageView6);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "DEVELO", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onRestart(){
        super.onRestart();

        if(startGPS()){
            downloadData(lastLocation);
        }
    }

    @Override
    public void onRefresh() {

        if (checkConnection()) {

            data.clear();

            if(startGPS()){
                downloadData(lastLocation);
            }

        } else {
            Toast.makeText(MainActivity.this, R.string.NO_INTERNET, Toast.LENGTH_LONG).show();
        }
        
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (lastLocation != null && data != null) {
            Intent intentService = new Intent(getApplicationContext(), RequestService.class);
            intentService.putExtra("latitude", lastLocation.getLatitude());
            intentService.putExtra("longitude", lastLocation.getLongitude());

            Log.d("last_Earthquake_Id", data.get(0).getId());

            intentService.putExtra("last_Earthquake_Id", data.get(0).getId());

            startService(intentService);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        stopGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGPS();
            } else {
                downloadData(null);
            }
        }
    }

    /**
     * Check Internet connection
     */
    private boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    /**
     * Alert Message Gps
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.GRANT_GPS)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.NO_THX, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        downloadData(null);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Start Location Service by GPS and Network provider.
     */
    private boolean startGPS() {

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        int check = ContextCompat
                .checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (check == PackageManager.PERMISSION_GRANTED) {

            // getting GPS status
            boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {

                buildAlertMessageNoGps();

            } else if (manager != null) {

                manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
                manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);

                return true;
            }

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        return false;

    }


    /**
     * Stop Location service.
     */
    private void stopGPS() {

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager != null) manager.removeUpdates(listener);

    }

    /**
     * Download Data by Volley
     */
    private void downloadData(Location location) {

        Toast.makeText(MainActivity.this, R.string.DOWNLOAD, Toast.LENGTH_SHORT).show();


        this.lastLocation = location;

        VolleyRequest.getInstance(getApplicationContext())
                .downloadEarthquakes(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Parse to JSON
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();


                        try {

                            JSONObject jsonObject = new JSONObject(xmlToJson.toFormattedString());

                            JSONObject quakeml = jsonObject.getJSONObject("q:quakeml");

                            JSONObject eventParameters = quakeml.getJSONObject("eventParameters");

                            JSONArray jsonRoot = eventParameters.getJSONArray("event");

                            for (int i = 0; i < jsonRoot.length(); i++) {

                                JSONObject item = jsonRoot.getJSONObject(i);

                                //region
                                JSONObject description = item.getJSONObject("description");
                                String region = description.getString("text");

                                //ID
                                String preferredOriginID = item.getString("preferredOriginID");
                                String id = preferredOriginID.substring(54);

                                //date & time
                                JSONObject creationInfo = item.getJSONObject("creationInfo");
                                String time = creationInfo.getString("creationTime");
                                LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);

                                //magnitude
                                JSONObject magnitude = item.getJSONObject("magnitude");
                                JSONObject mag = magnitude.getJSONObject("mag");
                                double ML = mag.getDouble("value");

                                //latitude
                                JSONObject origin = item.getJSONObject("origin");
                                JSONObject latitude = origin.getJSONObject("latitude");
                                double lat = latitude.getDouble("value");

                                //longitude
                                JSONObject longitude = origin.getJSONObject("longitude");
                                double lon = longitude.getDouble("value");

                                data.add(new Earthquake(id,ML, region, lat, lon, localDateTime));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Refresh list because the adapter data are changed
                        if (adapter != null) adapter.notifyDataSetChanged();
                    }
                }, location);
    }


    /**
     * Location Listener
     */
    public class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    }

}
