package com.fcfruit.zombiesmash.powerups.explodable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.fcfruit.zombiesmash.Environment;
import com.fcfruit.zombiesmash.effects.Fire;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;

import java.util.ArrayList;

/**
 * Created by Lucas on 2018-01-07.
 */

public class Molotov extends Explodable
{

    private int numFire = 10;

    public Molotov(Body body, ArrayList<Joint> joints)
    {
        super(body, joints);
    }

    public Molotov(Sprite sprite, Body physicsBody, ArrayList<Joint> joints, ContainerEntityInterface containerEntity)
    {
        super(sprite, physicsBody, joints, containerEntity);
    }

    public void setNumFire(int num_fire)
    {
        this.numFire = num_fire;
    }

    public int getNumFire(){return this.numFire;}

    private void createFire()
    {
        double angle;
        Vector2 velocity;
        Fire fire;
        for(int i = 0; i < numFire; i++)
        {
            angle = (float) Math.toRadians((i / (float) numFire) * 180 - 90);
            velocity = new Vector2((float) Math.sin(angle), (float) Math.cos(angle));
            fire = Environment.firePool.getFire(this.getPosition(), velocity.scl(0.1f));
            Environment.drawableAddQueue.add(fire);
        }
    }

    @Override
    public void explode()
    {
        this.createFire();

        if(!this.getState().equals("detached"))
        {
            this.setState("waiting_for_detach");
            Environment.detachableEntityDetachQueue.add(this);
        }

        this.getPhysicsBody().setActive(false);
        this.getPhysicsBody().setTransform(99, 99, 0);

        Environment.drawableRemoveQueue.add(this);
        this.destroy();
        this.dispose();
    }
}
