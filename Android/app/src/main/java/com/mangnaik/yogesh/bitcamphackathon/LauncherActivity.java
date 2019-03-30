package com.mangnaik.yogesh.bitcamphackathon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
                        new AlertDialog.Builder(LauncherActivity.this)
                                .setTitle("Success")
                                .setMessage("Updated the database successfully")
                                .setPositiveButton(android.R.string.yes, null)
                                .show();
                    }
                    else{
                        new AlertDialog.Builder(LauncherActivity.this)
                                .setTitle("Failure")
                                .setMessage("Failed to download the database")
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    }
                }
            });
        });
    }
}
