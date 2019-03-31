package com.mangnaik.yogesh.bitcamphackathon;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private Thread timerThread;
    private String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerThread = new Thread() {
            public void run() {
                try {
                    sleep(350);
                    requestPermissions(perms, permsRequestCode);
                } catch (InterruptedException e) {
                    finish();
                }
            }
        };
        timerThread.start();
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean readStorageAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if (readStorageAccepted && writeStorageAccepted) {
                    Intent intent = new Intent(SplashActivity.this, LauncherActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SplashActivity.this, "Please allow storage access", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timerThread.interrupt();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerThread.interrupt();
    }
}
