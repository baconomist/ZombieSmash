package com.fcfruit.zombiesmash.release.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.release.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.release.Environment;
import com.fcfruit.zombiesmash.release.physics.Physics;
import com.fcfruit.zombiesmash.release.entity.interfaces.DrawableEntityInterface;

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

    private float forceForDetach;

    public DetachableEntity(ArrayList<Joint> joints, ContainerEntityInterface containerEntity, DetachableEntityInterface instance)
    {
        this.joints = joints;
        this.containerEntity = containerEntity;
        this.instance = instance;

        this.forceForDetach = 100f;

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
        Gdx.app.debug("destroying joints...", "detachableEntity detach");
        for(Joint joint : this.joints)
        {
            Environment.physics.destroyJoint(joint);
        }
        this.joints = null;
        if(this.containerEntity != null)
        {
            this.containerEntity.detach(instance);
            Environment.level.addDrawableEntity((DrawableEntityInterface) instance);
        }
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
                //Gdx.app.debug("reaction force", ""+joint.getReactionForce(1f / Physics.STEP_TIME));
                if (Math.abs(joint.getReactionForce(1f / Physics.STEP_TIME).x) > this.forceForDetach || Math.abs(joint.getReactionForce(1f / Physics.STEP_TIME).y) > this.forceForDetach)
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

    @Override
    public void setForceForDetach(float force)
    {
        this.forceForDetach = force;
    }
}
