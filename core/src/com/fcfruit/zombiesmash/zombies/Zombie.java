package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.Skeleton;
import com.fcfruit.zombiesmash.BodyPhysics;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-07-21.
 */

public class Zombie{

    public Body body;

    public float mass;

    public int touchedLimb;

    public boolean isHanging;

    public boolean isPhysicsEnabled;

    public Zombie() {

        body = new Body(this);

        mass = 4;

        isHanging = false;

        isPhysicsEnabled = true;

    }


    public void update(float delta){

        // Has to be called before everything, or else some instructions to skeleton will not function.
        // I.E. skeleton.findBone("left_arm").setRotation(90); will not work before this update call
        body.update(delta);


        if(body.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()) && Gdx.input.isTouched() && !isDraging()) {
            isHanging = true;
            touchedLimb = body.getTouchedLimb(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        } else if (!isDraging() && !Gdx.input.isTouched()){
            isHanging = false;
            touchedLimb = 0;
        }

        // Simplified if statement.
        body.isGravityEnabled = !isHanging;
        if(isHanging) {
            body.hangFromLimb(touchedLimb, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        }

        Gdx.app.log("touchedlimb", ""+touchedLimb);



    }

    private boolean isDraging(){

        if(Gdx.input.getDeltaX() != 0f || Gdx.input.getDeltaY() != 0f) {

            return Gdx.input.getDeltaX() <= 500 && Gdx.input.getDeltaX() >= -500 && Gdx.input.getDeltaY() <= 500 && Gdx.input.getDeltaY() >= -500;

        }

        return false;

    }

    public void setPosition(float x, float y){
        body.setPosition(x, y);
    }

}








