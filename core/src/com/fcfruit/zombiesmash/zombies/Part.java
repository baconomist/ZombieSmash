package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;

/**
 * Created by Lucas on 2017-08-01.
 */

public class Part{

    private String name;

    private Body box2DBody;

    private ZombieBody body;

    private Polygon polygon;
    
    private Skeleton bodySkeleton;

    public float mass;

    public Vector2 attachementPoint;

    public boolean isAttached;


    public Part(String nm, ZombieBody b){
        name = nm;

        body = b;
        
        bodySkeleton = body.getSkeleton();

        mass = 0.5f;

        isAttached = true;

    }

    public void update(){
        if(isAttached){
            if(name.equals("torso") && body.isPhysicsEnabled){
                //Do physics for whole body

            }
        }
        //Update part polygon
        if(polygon != null) {
            polygon.setPosition(this.getWorldX() + body.getOffset(name)[0], this.getWorldY() + body.getOffset(name)[1]);
            polygon.setOrigin(bodySkeleton.getRootBone().getX(), bodySkeleton.getRootBone().getY());
            polygon.setRotation(this.getWorldRotationX());
        }

    }

    public Polygon getPolygon(){
        return polygon;
    }

    public String getName(){
        return name;
    }

    public float getWidth(){
        return ((RegionAttachment)bodySkeleton.findSlot(name).getAttachment()).getWidth();
    }

    public float getHeight(){
        return ((RegionAttachment)bodySkeleton.findSlot(name).getAttachment()).getHeight();
    }

    public float getWorldX(){
        return bodySkeleton.findBone(name).getWorldX();
    }

    public float getWorldY(){
        return bodySkeleton.findBone(name).getWorldY();
    }

    public float getWorldRotationX(){
        return bodySkeleton.findBone(name).getWorldRotationX();
    }

    public void setScale(){

    }

    public void setPolygon(Polygon poly){
        polygon = poly;
    }

    public void setRotation(float degrees){
        bodySkeleton.findBone(name).setRotation(degrees);
    }




}
