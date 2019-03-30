package com.mangnaik.yogesh.bitcamphackathon;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Yogesh Mangnaik on 3/30/2019.
 */
class NetworkHelper {
    static void downloadFile(Context context, String url) {
        // TODO handle the error
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                response -> {
                    try {
                        if (response!=null) {
                            File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/JustPoint");
                            if(!dir.exists())
                                dir.mkdirs();
                            File imageFile = new File(dir.getAbsoluteFile()+"/"+Constants.databaseFileName);
                            FileOutputStream stream = new FileOutputStream(imageFile);

                            try {
                                stream.write(response);
                            } finally {
                                stream.close();
                            }
                        }
                    } catch (Exception e) {
                        Log.d("ERROR!!", "NOT DOWNLOADED!!!");
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context, new HurlStack());
        mRequestQueue.add(request);
    }

    static void getRestaurantDetails(int restaurantId, CallbackInterface callback, Context context){
        String url = Constants.restDetailsIP + Constants.restDetailsURL + restaurantId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Restaurant restaurant = parseJson(response);
                        callback.callback(restaurant);
                    }
                }, error -> System.out.println(error));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private static Restaurant parseJson(JSONObject response) {
        Restaurant restaurant = new Restaurant();
        try {
            JSONObject object = response.getJSONObject("result");
            restaurant.name = object.getString("name");
            restaurant.imageURL = object.getString("featured_image");
            restaurant.costForTwo = object.getString("average_cost_for_two");
            restaurant.zomatoLink = object.getString("url");


            JSONObject ratingObject = object.getJSONObject("user_rating");
            restaurant.aggRating = ratingObject.getString("aggregate_rating");
            restaurant.ratingText = ratingObject.getString("rating_text");
            restaurant.hexColor = ratingObject.getString("rating_color");
            System.out.println(restaurant);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurant;
    }
}
