package com.fcfruit.zombiesmash.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2017-11-18.
 */

public class Objective {

    Body body;


    public void setPosition(float x, float y){
        this.body.setTransform(x, y, this.body.getAngle());
    }

}
