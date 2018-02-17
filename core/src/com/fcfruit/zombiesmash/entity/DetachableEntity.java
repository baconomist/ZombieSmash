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

import java.util.ArrayList;


/**
 * Created by Lucas on 2018-01-06.
 */

public class DetachableEntity implements DetachableEntityInterface
{
    private ArrayList<Joint> joints;
    private String state;

    private ContainerEntityInterface containerEntity;

    private DetachableEntityInterface instance;

    public DetachableEntity(ArrayList<Joint> joints, ContainerEntityInterface containerEntity, DetachableEntityInterface instance)
    {
        this.joints = joints;
        this.containerEntity = containerEntity;
        this.instance = instance;

        // Assumes attached state
        this.setState("attached");
    }

    public DetachableEntity(ArrayList<Joint> joints)
    {
        this.joints = joints;
    }

    @Override
    public void detach()
    {
        for(Joint joint : this.joints)
        {
            Environment.physics.getWorld().destroyJoint(joint);
        }
        this.joints = null;
        this.containerEntity.detach(instance);
        Environment.level.addDrawableEntity((DrawableEntityInterface) instance);
        this.setState("detached");
    }

    @Override
    public void setState(String state)
    {
        if (state.equals("attached") || state.equals("waiting_for_detach") || state.equals("detached"))
            this.state = state;
        else
            throw new AssertionError();
        
    }

    public String getState()
    {
        return this.state;
    }

    @Override
    public boolean shouldDetach()
    {
        if (this.joints != null)
        {
            for(Joint joint : this.joints)
            {
                //Gdx.app.log("reaction force", ""+joint.getReactionForce(1f / Physics.STEP_TIME));
                if (Math.abs(joint.getReactionForce(1f / Physics.STEP_TIME).x) > 100f || Math.abs(joint.getReactionForce(1f / Physics.STEP_TIME).y) > 100f)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<Joint> getJoints()
    {
        return this.joints;
    }

    @Override
    public ContainerEntityInterface getContainer()
    {
        return this.containerEntity;
    }

}
