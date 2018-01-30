package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.physics.Physics;

/**
 * Created by Lucas on 2018-01-06.
 */

public class DetachableEntity implements com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface
{
    private Joint[] joints;
    private String state;

    private ContainerEntity containerEntity;

    private int id = Environment.i + 1;

    public DetachableEntity(Joint[] joints, ContainerEntity containerEntity){
        this.joints = joints;
        this.containerEntity = containerEntity;

        // Assumes attached state
        this.setState("attached");

        Environment.i++;
    }

    public DetachableEntity(Joint[] joints){
        this.joints = joints;
    }

    @Override
    public void detach()
    {

        for(Joint joint : this.joints)
        {
            Gdx.app.log("detach", "detach"+id + " "+joint);
            Environment.physics.getWorld().destroyJoint(joint);
        }
        this.joints = null;
        this.containerEntity.detach(this);
        this.setState("detached");
    }

    @Override
    public void setState(String state){
        if(state.equals("attached") && this.joints != null){
            this.state = "attached";
        }
        else if(state.equals("waiting_for_detach") && this.joints != null){
            this.state = "waiting_for_detach";
        }
        else if(state.equals("detached") && this.joints == null){
            this.state = "detached";
        }
    }

    public String getState(){
        return this.state;
    }

    @Override
    public boolean shouldDetach()
    {
        if(this.joints != null)
        {
            for(Joint joint : this.joints)
            {
                if (joint.getReactionForce(1f / Physics.STEP_TIME).x > 20 || joint.getReactionForce(1f / Physics.STEP_TIME).x > 20
                        || joint.getReactionForce(1f / Physics.STEP_TIME).y > 20 || joint.getReactionForce(1f / Physics.STEP_TIME).y > 20){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ContainerEntity getContainer()
    {
        return this.containerEntity;
    }
}
