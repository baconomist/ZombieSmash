package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;

/**
 * Created by Lucas on 2018-01-06.
 */

public class DetachableEntity implements com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface
{
    private Joint joint;
    private String state;

    private ContainerEntity containerEntity;

    public DetachableEntity(Joint joint, ContainerEntity containerEntity){
        this.joint = joint;
        this.containerEntity = containerEntity;

        // Assumes attached state
        this.state = "attached";
    }

    public DetachableEntity(Joint joint){
        this.joint = joint;
    }

    @Override
    public void detach()
    {
        Environment.physics.getWorld().destroyJoint(joint);
        joint = null;
        if(this.containerEntity != null)
        {
            this.containerEntity.detach(this);
        }
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

    @Override
    public boolean shouldDetach()
    {
        return this.joint.getReactionForce(1f / Physics.STEP_TIME).x > 20 || this.joint.getReactionForce(1f / Physics.STEP_TIME).x > 20
                || this.joint.getReactionForce(1f / Physics.STEP_TIME).y > 20 || this.joint.getReactionForce(1f / Physics.STEP_TIME).y > 20;
    }
}
