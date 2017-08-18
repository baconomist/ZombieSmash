package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;

/**
 * Created by Lucas on 2017-08-01.
 */

public class Part{

    private String name;

    private ZombieBody body;

    public float mass;

    public Vector2 attachementPoint;

    public boolean isAttached;


    public Part(String nm, ZombieBody b){
        name = nm;

        body = b;

        mass = 0.5f;

        isAttached = true;

    }

    public void update(float delta){

    }

    public void setScale(){

    }

    public void setRotation(float degrees){
        body.skeleton.findBone(name).setRotation(degrees);
    }

    public String getName(){
        return name;
    }

    public float getWidth(){
        return ((RegionAttachment)body.skeleton.findSlot(name).getAttachment()).getWidth();
    }

    public float getHeight(){
        return ((RegionAttachment)body.skeleton.findSlot(name).getAttachment()).getHeight();
    }

    public float getWorldX(){
        return body.skeleton.findBone(name).getWorldX();
    }

    public float getWorldY(){
        return body.skeleton.findBone(name).getWorldY();
    }

    public float getWorldRotationX(){
        return body.skeleton.findBone(name).getWorldRotationX();
    }



}
