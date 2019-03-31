package com.mangnaik.yogesh.bitcamphackathon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class LauncherActivity extends AppCompatActivity {

    ProgressDialog dialog;

    Handler handler;

    TextView tv1;
    TextView tv2;
    TextView tv3;

    @Override
    protected void onResume() {
        tv1.setScaleX(1.0f);
        tv1.setScaleY(1.0f);

        tv2.setScaleX(1.0f);
        tv2.setScaleY(1.0f);

        tv3.setScaleX(1.0f);
        tv3.setScaleY(1.0f);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update: {
                System.out.println("Update Called");
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
                break;
            }
            // case blocks for other MenuItems (if any)
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        handler = new Handler(Looper.getMainLooper());
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);

        tv1 = findViewById(R.id.tv_one);
        tv2 = findViewById(R.id.tv_two);
        tv3 = findViewById(R.id.tv_three);

        tv1.setTranslationY(2000f);
        tv2.setTranslationY(2000f);
        tv3.setTranslationY(2000f);
        tv1.setScaleX(0.0f);
        tv2.setScaleX(0.0f);
        tv3.setScaleX(0.0f);
        tv1.setScaleY(0.0f);
        tv2.setScaleY(0.0f);
        tv3.setScaleY(0.0f);

        handler.postDelayed((Runnable) () -> tv1.animate()
                .translationY(0f)
                .setDuration(300)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(new DecelerateInterpolator())
                .start(), 100);

        handler.postDelayed((Runnable) () -> tv2.animate()
                .translationY(0f)
                .setDuration(300)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(new DecelerateInterpolator())
                .start(),  300);

        handler.postDelayed((Runnable) () -> tv3.animate()
                .translationY(0f)
                .setDuration(300)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(new DecelerateInterpolator())
                .start(), 500);

        tv1.setOnClickListener(v -> {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.vuforia.engine.CoreSamplesUnity");
            startActivity(launchIntent);
        });

        tv2.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            tv1.animate()
                    .scaleX(0.0f)
                    .scaleY(0.0f)
                    .setDuration(100)
                    .start();
            tv2.animate()
                    .scaleX(0.0f)
                    .scaleY(0.0f)
                    .setDuration(100)
                    .start();
            tv3.animate()
                    .scaleX(0.0f)
                    .scaleY(0.0f)
                    .setDuration(100)
                    .start();
            handler.postDelayed(() -> startActivity(i), 200);
        });

        tv3.setOnClickListener(v -> {
            //TODO : launch museum app
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.nisarg.Museum");
            startActivity(launchIntent);
        });

//        Button update = findViewById(R.id.update);
//        update.setOnClickListener(v -> {
//            dialog.setTitle("Downloading....");
//            dialog.setMessage("Downloading the Database");
//            dialog.setIndeterminate(true);
//            dialog.show();
//            NetworkHelper.downloadFile(this, Constants.imgdbIP + Constants.imgdbURL, new DefaultCallback() {
//                @Override
//                public void callback(boolean success) {
//                    if(success){
//                        dialog.hide();
//                        new AlertDialog.Builder(LauncherActivity.this)
//                                .setTitle("Success")
//                                .setMessage("Updated the database successfully")
//                                .setPositiveButton(android.R.string.yes, null)
//                                .show();
//                    }
//                    else{
//                        new AlertDialog.Builder(LauncherActivity.this)
//                                .setTitle("Failure")
//                                .setMessage("Failed to download the database")
//                                .setNegativeButton(android.R.string.no, null)
//                                .show();
//                    }
//                }
//            });
//        });
    }


}
