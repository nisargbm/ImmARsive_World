package com.mangnaik.yogesh.bitcamphackathon;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent i = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(i);
        });

        Button update = findViewById(R.id.update);
        update.setOnClickListener(v -> downloadFile());
    }

    private void downloadFile() {
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, "http://192.168.137.184:4000/fetchIMDB",
                response -> {

                    // TODO handle the response
                    try {
                        if (response!=null) {
                            String name="image.imgdb";
                            File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                            if(!dir.exists())
                                dir.mkdirs();

                            File imageFile = new File(dir.getAbsoluteFile()+"/"+name);

                            FileOutputStream stream = new FileOutputStream(imageFile);

                            try {
                                stream.write(response);
                            } finally {
                                stream.close();
                            }
                            Toast.makeText(LauncherActivity.this, "Download complete.", Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.d("ERROR!!", "NOT DOWNLOADED");
                        e.printStackTrace();
                    }
                }, error -> {
                    // TODO handle the error
                    error.printStackTrace();
                }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        mRequestQueue.add(request);
    }
}
