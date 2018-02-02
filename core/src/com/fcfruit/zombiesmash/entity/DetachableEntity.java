package com.fcfruit.zombiesmash.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DrawableEntityInterface;
import com.fcfruit.zombiesmash.physics.Physics;
import com.fcfruit.zombiesmash.zombies.NewPart;


/**
 * Created by Lucas on 2018-01-06.
 */

public class DetachableEntity implements DetachableEntityInterface
{
    private Joint joint;
    private String state;

    private ContainerEntityInterface containerEntity;

    private Object compositionObject;

    public DetachableEntity(Joint joint, ContainerEntityInterface containerEntity, Object compositionObject){
        this.joint = joint;
        this.containerEntity = containerEntity;
        this.compositionObject = compositionObject;

        // Assumes attached state
        this.setState("attached");

    }

    public Joint getJoint(){
        return this.joint;
    }

    public DetachableEntity(Joint joint){
        this.joint = joint;
    }

    @Override
    public void detach()
    {
        Environment.physics.getWorld().destroyJoint(this.joint);
        this.joint = null;
        this.containerEntity.detach((DetachableEntityInterface) this.compositionObject);
        Environment.level.addDrawableEntity((DrawableEntityInterface) this.compositionObject);
        this.setState("detached");
    }

    @Override
    public void setState(String state){
        if(state.equals("attached") && this.joint != null){
            this.state = "attached";
        }
        else if(state.equals("waiting_for_detach") && this.joint != null){
            this.state = "waiting_for_detach";
        }
        else if(state.equals("detached") && this.joint == null){
            this.state = "detached";
        }
    }

    public String getState(){
        return this.state;
    }

    @Override
    public boolean shouldDetach()
    {
        if(joint != null)
        {
            if (joint.getReactionForce(1f / Physics.STEP_TIME).x > 20 || joint.getReactionForce(1f / Physics.STEP_TIME).x > 20
                    || joint.getReactionForce(1f / Physics.STEP_TIME).y > 20 || joint.getReactionForce(1f / Physics.STEP_TIME).y > 20)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ContainerEntityInterface getContainer()
    {
        return this.containerEntity;
    }
}
