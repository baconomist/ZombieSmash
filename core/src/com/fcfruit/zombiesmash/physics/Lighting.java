package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;
import com.fcfruit.zombiesmash.Environment;

import java.util.ArrayList;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Created by Lucas on 2017-11-11.
 */



public class Lighting {

    private ArrayList<Light> lights;
    private RayHandler rayHandler;

    World world;

    public Lighting(World w){
        world = w;
        if(Environment.settings.isEnableLight()) {
            rayHandler = new RayHandler(world);
            rayHandler.setAmbientLight(0f, 0f, 0f, Environment.settings.getBrightness()/100f);
            lights = new ArrayList<Light>();
            lights.add(new PointLight(rayHandler, Environment.settings.getLightIntensity(), Color.WHITE, 2, 3, 3));
        }
    }

    public void update(){
        if(rayHandler != null && Environment.settings.isEnableLight()) {
            rayHandler.setCombinedMatrix(Environment.physicsCamera);
            rayHandler.updateAndRender();
        }
        else if (Environment.settings.isEnableLight()){
            rayHandler = new RayHandler(world);
            rayHandler.setAmbientLight(0f, 0f, 0f, 0.35f);
            lights = new ArrayList<Light>();
            lights.add(new PointLight(rayHandler, Environment.settings.getLightIntensity(), Color.WHITE, 2, 3, 3));
        }
    }

}
