package com.mangnaik.yogesh.bitcamphackathon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        dialog = new ProgressDialog(this);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent i = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(i);
        });

        Button details = findViewById(R.id.deatils);
        details.setOnClickListener(v -> NetworkHelper.getRestaurantDetails(35092, restaurant -> {}, this));

        Button update = findViewById(R.id.update);
        update.setOnClickListener(v -> {
            dialog.setTitle("Downloading....");
            dialog.setMessage("Downloading the Database");
            dialog.setIndeterminate(true);
            dialog.show();
            NetworkHelper.downloadFile(this, Constants.imgdbIP + Constants.imgdbURL, new DefaultCallback() {
                @Override
                public void callback(boolean success) {
                    if(success){
                        dialog.hide();
                    }
                    else{
                        new AlertDialog.Builder(LauncherActivity.this)
                                .setTitle("Alert")
                                .setMessage("Failed to download the database")
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    }
                }
            });
        });
    }
}
