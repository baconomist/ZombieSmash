package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Objective {

    public Polygon polygon;

    float health = 100;

    public void setPosition(float x, float y){
        Vector3 pos = Environment.gameCamera.unproject(Environment.physicsCamera.project(new Vector3(x, y, 0)));
        this.polygon.setPosition(pos.x, Environment.gameCamera.viewportHeight - pos.y);
    }

    public void onHit(){
        health--;
        Gdx.app.log("Objective", "health: "+health);
    }

    public float getHealth(){return health;}

}
