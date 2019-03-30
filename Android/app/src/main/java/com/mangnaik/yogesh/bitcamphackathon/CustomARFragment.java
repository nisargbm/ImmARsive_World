package com.mangnaik.yogesh.bitcamphackathon;

import android.util.Log;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

/**
 * Created by Yogesh Mangnaik on 3/30/2019.
 */
public class CustomARFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {
        getPlaneDiscoveryController().setInstructionView(null);
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);
        config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
        session.configure(config);
        getArSceneView().setupSession(session);

        if (((ARActivity)getActivity()).setupAugmentedImagesDb(config, session)) {
            Log.d("SetupAugImgDb", "Success");
        } else {
            Log.e("SetupAugImgDb","Faliure setting up db");
        }
        return config;
    }
}
