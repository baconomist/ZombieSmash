package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    private Body box2DBody;

    private ZombieBody body;

    private Polygon polygon;

    private Body physicsBody;

    private RevoluteJoint joint;
    
    private Skeleton bodySkeleton;

    public Vector2 attachementPoint;

    public boolean isAttached;

    public Part(String nm, ZombieBody b){
        name = nm;

        body = b;
        
        bodySkeleton = body.getSkeleton();

        isAttached = true;

    }

    public void update(){
        if(isAttached){
            if(name.equals("torso") && body.isPhysicsEnabled){
                //Do physics for whole body

            }
            else if(!body.isPhysicsEnabled){
                updatePhysicsBody();
            }
        }
    }

    private void updatePhysicsBody(){
        if(physicsBody != null) {
            if(isAttached) {

            }
            else{
                // Update using without using body offsets
            }
        }
    }

    public void createJoint(Body b, float[] offsets, World world){
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.initialize(physicsBody, b, new Vector2(offsets[0], offsets[1]));
        jointDef.enableLimit = false;
        jointDef.enableMotor = true;
        jointDef.collideConnected = false;
        joint = (RevoluteJoint) world.createJoint(jointDef);
    }

    public void setRotation(float degrees){
        bodySkeleton.findBone(name).setRotation(degrees);
    }

    public void setPhysicsBody(Body b){
        physicsBody = b;
    }

    public Body getPhysicsBody(){
        return physicsBody;
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

}
