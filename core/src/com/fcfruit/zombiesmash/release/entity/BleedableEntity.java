package com.fcfruit.zombiesmash.release.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.fcfruit.zombiesmash.release.effects.Blood;
import com.fcfruit.zombiesmash.release.entity.interfaces.BleedableEntityInterface;
import com.fcfruit.zombiesmash.release.entity.interfaces.DetachableEntityInterface;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 2018-02-02.
 */

public class BleedableEntity implements BleedableEntityInterface
{

    private DetachableEntityInterface detachableEntity;

    private ArrayList<Body> bleedableBodies;
    private HashMap<Body, Vector2> bleedPositions;

    private ArrayList<Blood> blood;


    private double timeBeforeBlood = 100;
    private double bloodTimer = System.currentTimeMillis();

    public BleedableEntity(DetachableEntityInterface detachableEntity)
    {

        this.detachableEntity = detachableEntity;

        this.bleedableBodies = new ArrayList<Body>();
        this.bleedPositions = new HashMap<Body, Vector2>();
        this.blood = new ArrayList<Blood>();

        for (Joint joint : this.detachableEntity.getJoints())
        {
            if (joint instanceof RevoluteJoint)
            {
                if (!this.bleedableBodies.contains(joint.getBodyA()))
                {
                    this.bleedableBodies.add(joint.getBodyA());
                }
                if (!this.bleedableBodies.contains(joint.getBodyB()))
                {
                    this.bleedableBodies.add(joint.getBodyB());
                }

                this.bleedPositions.put(joint.getBodyA(), ((RevoluteJoint) joint).getLocalAnchorA());
                this.bleedPositions.put(joint.getBodyB(), ((RevoluteJoint) joint).getLocalAnchorB());
            }
        }

    }

    @Override
    public void draw(SpriteBatch batch)
    {
        for (Blood blood : this.blood)
        {
            blood.draw(batch);
        }
    }

    @Override
    public void update(float delta)
    {

        ArrayList<Blood> copy = new ArrayList<Blood>();
        for (Blood blood : this.blood)
        {
            copy.add(blood);
        }
        for (Blood blood : copy)
        {
            if (blood.readyForDestroy)
            {
                blood.destroy();
                this.blood.remove(blood);
            }
        }

        if (this.detachableEntity.getState().equals("detached"))
        {
            if (System.currentTimeMillis() - this.bloodTimer > this.timeBeforeBlood)
            {
                for (Body physicsBody : bleedableBodies)
                {
                    this.blood.add(new Blood(physicsBody.getPosition().x, physicsBody.getPosition().y, this.bleedPositions.get(physicsBody).y, this.bleedPositions.get(physicsBody).x, (float) Math.toDegrees(physicsBody.getAngle()) + 90));
                }
                this.bloodTimer = System.currentTimeMillis();
            }
        } else
        {
            this.bloodTimer = System.currentTimeMillis();
        }
    }

}
