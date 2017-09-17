package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;

/**
 * Created by Lucas on 2017-08-01.
 */

public class Part{

    private String name;

    public Sprite sprite;

    private ZombieBody body;

    public Body physicsBody;

    private RevoluteJoint joint;

    public boolean isAttached;

    public Part(String nm, Sprite s, Body b, ZombieBody zombody){
        name = nm;

        sprite = s;

        physicsBody = b;

        body = zombody;

        isAttached = true;

    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void update(){

    }

    private void updatePhysicsBody(){
        if(physicsBody != null) {
            if(!isAttached) {

            }
        }
    }

    public void remove(){
        if(body != null) {
            body.parts.remove(name);
            body = null;
        }
    }

    public String getName(){
        return name;
    }

}
