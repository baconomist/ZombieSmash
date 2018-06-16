package com.fcfruit.zombiesmash.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
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
            rayHandler.setAmbientLight(0f, 0f, 0f, 0.7f);
            lights = new ArrayList<Light>();
            Vector3 pos = new Vector3(11.1f, 4.3f, 0f);
            lights.add(new PointLight(rayHandler, Environment.settings.getLightIntensity(), Color.WHITE, 2.5f, pos.x, pos.y));

        }
    }

    public void update(){
        if(rayHandler != null && Environment.settings.isEnableLight()) {
            rayHandler.setCombinedMatrix(Environment.physicsCamera);
            rayHandler.update();
        }
        else if (Environment.settings.isEnableLight()){
            rayHandler = new RayHandler(world);
            rayHandler.setAmbientLight(0f, 0f, 0f, 0.7f);
            lights = new ArrayList<Light>();
            lights.add(new PointLight(rayHandler, Environment.settings.getLightIntensity(), Color.WHITE, 2, 3, 3));
        }
    }

    public void draw(){
        if(rayHandler != null && Environment.settings.isEnableLight()) {
            rayHandler.setCombinedMatrix(Environment.physicsCamera);
            rayHandler.render();
        }
    }

}
