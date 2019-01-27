package com.univaq.loreand.earthquake.utility;


import android.content.Context;
import android.location.Location;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * MobileProgramming2018
 * Created by leonardo on 30/11/2018.
 */
public class VolleyRequest {

    private RequestQueue queue;

    private static VolleyRequest instance = null;

    public static VolleyRequest getInstance(Context context){
        return instance == null ? instance = new VolleyRequest(context) : instance;
    }

    private VolleyRequest(Context context){

        queue = Volley.newRequestQueue(context);
    }

    /**
     *Download the list of earthquakes, if the location is null download default list
     * */
    public void downloadEarthquakes(Response.Listener<String> listener, Location location){

        String url = "http://webservices.rm.ingv.it/fdsnws/event/1/query?format=xml";

        if(location!=null){

            url=url+"&lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&maxradiuskm=300&format=xml";

        }

        StringRequest request = new StringRequest(
                StringRequest.Method.GET,
                url,
                listener,
                null);
        queue.add(request);
    }
}
