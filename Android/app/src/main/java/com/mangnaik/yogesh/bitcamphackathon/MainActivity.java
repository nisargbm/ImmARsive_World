package com.mangnaik.yogesh.bitcamphackathon;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements ARActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    CustomARFragment arFragment;

    boolean shouldAddModel = true;
    Handler handler;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(checkIsSupportedDeviceOrFinish(this));

        arFragment = (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        handler = new Handler(Looper.getMainLooper());

        assert arFragment != null;
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        view = getLayoutInflater().inflate(R.layout.layout_restaurant, null);
    }

    @Override
    public boolean setupAugmentedImagesDb(Config config, Session session) {
        try{
            System.out.println("Setting up Augmented image database");
            AugmentedImageDatabase augmentedImageDatabase;
            String name="image.imgdb";
            File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/JustPoint");
            File imageFile = new File(dir.getAbsoluteFile()+"/"+name);
            InputStream inputStream = new FileInputStream(imageFile);
            //InputStream inputStream = getAssets().open("image.imgdb");
            augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, inputStream);
            config.setAugmentedImageDatabase(augmentedImageDatabase);
            System.out.println("Augmented image database set up");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        assert frame != null;
        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : augmentedImages) {
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
                if (shouldAddModel) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    String id = augmentedImage.getName().split("\\.")[0];
                    NetworkHelper.getRestaurantDetails(id, (Restaurant restaurant) -> {
//                        MainActivity.this.placeView(augmentedImage, restaurant);
                        NetworkHelper.getRestaurantReviews(this, id, restaurant, new CallbackInterface() {
                                @Override
                                public void callback(Restaurant rest) {
                                    MainActivity.this.placeView(augmentedImage, restaurant);
                                    System.out.println(restaurant);
                                }
                            });
                    }, this);
                    shouldAddModel = false;
                }
            }
        }
    }

    private void placeView(AugmentedImage augmentedImage, Restaurant restaurant){
        ViewRenderable.builder()
                .setView(this, view)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(arFragment, augmentedImage.createAnchor(augmentedImage.getCenterPose()), modelRenderable));

        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvPrice = view.findViewById(R.id.tv_fortwo);
        TextView tvRating = view.findViewById(R.id.tv_rating);
        TextView llReviews = view.findViewById(R.id.ll_reviews);
        TextView tvOffers = view.findViewById(R.id.ll_offers);
        LinearLayout ll_cover = view.findViewById(R.id.ll_cover);

        Glide.with(this).load(restaurant.imageURL).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                ll_cover.setBackground(resource);
            }
        });

        tvName.setText(restaurant.name);
        tvPrice.setText("Rs. : " + restaurant.costForTwo + " for Two");
        tvRating.setText(restaurant.aggRating+"\n"+restaurant.ratingText);
        LinearLayout layout = view.findViewById(R.id.rating_layout);
        tvOffers.setText("No ofers urrently available");
        layout.setBackgroundColor(Color.parseColor("#"+restaurant.hexColor));
        String s = "Reviews : \n";
        for(int i=0; i<restaurant.reviews.size() && i<3; i++){
            s += i + ") " + restaurant.reviews.get(i) + "\n\n";
        }
        llReviews.setText(s);

    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        //anchorNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0f,0f,-1f), 90f));
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());

        node.setWorldRotation(new Quaternion(Quaternion.axisAngle(Vector3.up(), 0f)));
        node.getScaleController().setMaxScale(0.1f);
        node.getScaleController().setMinScale(0.035f);
        node.setWorldRotation(Quaternion.axisAngle(new Vector3(1f, 0f, 0f), 270));
        node.setLocalPosition(new Vector3(0.12f,0f,-0.01f));

        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
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
