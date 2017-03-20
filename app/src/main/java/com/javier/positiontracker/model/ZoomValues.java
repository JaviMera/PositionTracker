package com.javier.positiontracker.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by javie on 3/20/2017.
 */

public class ZoomValues {

    private static Map<CameraLevel, Float> map = new HashMap<CameraLevel, Float>() {
        {
            put(CameraLevel.World, 1.0f);
            put(CameraLevel.Landmass, 5.0f);
            put(CameraLevel.City, 10.0f);
            put(CameraLevel.Streets, 15.0f);
            put(CameraLevel.Buildings, 20.0f);
        }
    };

    public static Float get(CameraLevel level) {

        return map.get(level);
    }
}
