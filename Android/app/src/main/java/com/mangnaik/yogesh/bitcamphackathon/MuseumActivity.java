package com.mangnaik.yogesh.bitcamphackathon;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class MuseumActivity extends AppCompatActivity implements ARActivity{

//    private static final String TAG = MainActivity.class.getSimpleName();
//    private static final double MIN_OPENGL_VERSION = 3.0;
//
//    CustomARFragment arFragment;
//    ModelRenderable renderable;
//
//    boolean shouldAddModel = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_museum);
//
//        arFragment = (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
//
//        assert arFragment != null;
//        ModelRenderable.builder()
//                .setSource(this, Uri.parse("objpainting.sfb"))
//                .build()
//                .thenAccept(renderable -> renderable = renderable)
//                .exceptionally(throwable -> {
//                    Toast toast =
//                            Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                    return null;
//                });
//
//        arFragment.setOnTapArPlaneListener(
//                (HitResult hitresult, Plane plane, MotionEvent motionevent) -> {
//                    if (renderable == null){
//                        return;
//                    }
//                    Anchor anchor = hitresult.createAnchor();
//                    AnchorNode anchorNode = new AnchorNode(anchor);
//                    anchorNode.setParent(arFragment.getArSceneView().getScene());
//                    TransformableNode lamp = new TransformableNode(arFragment.getTransformationSystem());
//                    lamp.setParent(anchorNode);
//                    lamp.setRenderable(renderable);
//                    lamp.select();
//                }
//        );
//    }

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    CustomARFragment arFragment;

    boolean shouldAddModel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(checkIsSupportedDeviceOrFinish(this));

        arFragment = (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        assert arFragment != null;
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
    }

    @Override
    public boolean setupAugmentedImagesDb(Config config, Session session){
        System.out.println("Setting up Augmented image database");
        AugmentedImageDatabase augmentedImageDatabase = null;
        String name="image.imgdb";
        File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/JustPoint");
        File imageFile = new File(dir.getAbsoluteFile()+"/"+name);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //InputStream inputStream = getAssets().open("image.imgdb");
        try {
            augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        System.out.println("Augmented image database set up");
        return true;
    }

    private Bitmap loadAugmentedImage() {
        System.out.println("Loading Augmented Images");
        try (InputStream is = getAssets().open("car_image.png")) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e("ImageLoad", "IO Exception", e);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : augmentedImages) {
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
                if (shouldAddModel) {
                    System.out.println("Found image : " + augmentedImage.getName());
                    System.out.println("Found ID : " + augmentedImage.getIndex());
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    //placeObject(arFragment, augmentedImage.createAnchor(augmentedImage.getCenterPose()), Uri.parse("Lamborghini_Aventador.sfb"));
                    shouldAddModel = false;
                }
            }
        }
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            //activity.finish();
            return false;
        }
        System.out.println("Working Correctly");
        return true;
    }
}
