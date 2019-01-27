package com.univaq.loreand.earthquake.activity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.univaq.loreand.earthquake.R;
import com.univaq.loreand.earthquake.model.Earthquake;
import com.univaq.loreand.earthquake.utility.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class RequestService extends IntentService {

    private final int notification_id = 1;

    private Earthquake newEarthquake;

    private Earthquake lastEarthquake;

    private final int TIME_UPDATE=10000;

    public RequestService() {
        super("RequestService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        notifyLocation("Earthquake run on backgroung");

        while(true)
        {
            String lastEarthquakeId = intent.getStringExtra("last_Earthquake_Id");
            lastEarthquake = new Earthquake(lastEarthquakeId);

            Log.d("last_Earthquake_Id_2",lastEarthquake.getId());

            double latitude = intent.getDoubleExtra("latitude",0);
            double longitude = intent.getDoubleExtra("longitude",0);


            if (latitude==0 && longitude == 0){
                downloadData(null);

            }else{
                Location location = new Location("current");
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                downloadData(location);
            }


            if(newEarthquake != null){

            //Log.d("CHECK_001 ","NEW:"+newEarthquake.getId()+" LAST:"+lastEarthquake.getId()+" RESULT:"+newEarthquake.getId().equals(lastEarthquake.getId()));

                if(!newEarthquake.getId().equals(lastEarthquake.getId())){
                    notifyLocation("Scossa!");
                }
            }

            try {
                Thread.sleep(TIME_UPDATE);
            }
            catch (InterruptedException e)
            { }
        }
    }

    /**
     * Download Data by Volley
     */
    private void downloadData(Location location){

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

                            JSONObject item = jsonRoot.getJSONObject(0);

                            //ID
                            String preferredOriginID = item.getString("preferredOriginID");
                            //Get id from substring at character 54
                            String id = preferredOriginID.substring(54);

                            newEarthquake = new Earthquake(id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },location);
    }


    /**
     * Publish a notify.
     *
     * @param message
     */
    private void notifyLocation(String message) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("myChannel", "Il Mio Canale", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLightColor(Color.argb(255, 255, 0, 0));
            if(notificationManager != null) notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), "myChannel");
        builder.setContentTitle(getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.ic_stat_slow_motion_video);
        builder.setContentText(message);
        builder.setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, 0);

        builder.setContentIntent(pendingIntent);

        Notification notify = builder.build();
        if(notificationManager != null) notificationManager.notify(notification_id, notify);
    }
}
