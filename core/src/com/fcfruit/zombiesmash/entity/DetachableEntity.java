package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Joint;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.fcfruit.zombiesmash.Environment;

/**
 * Created by Lucas on 2018-01-06.
 */

public class DetachableEntity implements DetachableEntityInterface
{
    private Joint joint;
    private String state;

    public DetachableEntity(Joint joint){
        this.joint = joint;
    }

    @Override
    public void detach()
    {
        Environment.physics.getWorld().destroyJoint(joint);
        joint = null;
        Environment.physics.addBody(this);
        this.setState("detached");
    }

    @Override
    public void setState(String state){
        if(state.equals("attached") && joint != null){
            this.state = "attached";
        }
        else if(state.equals("waiting_for_detach") && joint != null){
            this.state = "waiting_for_detach";
        }
        else if(state.equals("detached") && joint == null){
            this.state = "detached";
        }
    }

    public String getState(){
        return this.state;
    }

}
