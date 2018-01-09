package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2018-01-06.
 */

public class InteractiveGraphicsEntity implements InteractiveEntityInterface {
    
    private boolean isTouching;
    private int pointer;
    private Polygon polygon;

    public InteractiveGraphicsEntity(Polygon polygon){
        this.isTouching = false;
        this.pointer = -1;
        this.polygon = polygon;
    }

    @Override
    public void onTouchDown(float x, float y, int p) {

    }

    @Override
    public void onTouchDragged(float x, float y, int p) {

    }

    @Override
    public void onTouchUp(float x, float y, int p) {

    }

}
