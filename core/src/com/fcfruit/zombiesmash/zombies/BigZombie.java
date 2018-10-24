package com.fcfruit.zombiesmash.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.fcfruit.zombiesmash.entity.BleedablePoint;
import com.fcfruit.zombiesmash.entity.interfaces.ContainerEntityInterface;
import com.fcfruit.zombiesmash.entity.interfaces.DetachableEntityInterface;
import com.fcfruit.zombiesmash.zombies.parts.Bike;

import java.util.ArrayList;

/**
 * Created by Lucas on 2017-11-21.
 */

public class BigZombie extends Zombie
{

    public BigZombie(Integer id)
    {
        super(id);

        this.moveAnimation = "walk";

        this.detachableEntitiesToStayAlive.add("head");
        this.detachableEntitiesToStayAlive.add("left_armback");
        this.detachableEntitiesToStayAlive.add("left_armfront");
        this.detachableEntitiesToStayAlive.add("right_armback");
        this.detachableEntitiesToStayAlive.add("right_armfront");
        this.detachableEntitiesToStayAlive.add("left_legback");
        this.detachableEntitiesToStayAlive.add("left_legfront");
        this.detachableEntitiesToStayAlive.add("right_legback");
        this.detachableEntitiesToStayAlive.add("right_legfront");
        this.detachableEntitiesToStayAlive.add("bike");

        this.currentParts.add("head");
        this.currentParts.add("torso");
        this.currentParts.add("left_armback");
        this.currentParts.add("left_armfront");
        this.currentParts.add("right_armback");
        this.currentParts.add("right_armfront");
        this.currentParts.add("left_legback");
        this.currentParts.add("left_legfront");
        this.currentParts.add("right_legback");
        this.currentParts.add("right_legfront");
        this.currentParts.add("bike");

    }

    @Override
    protected void animationSetup()
    {
        super.animationSetup();
        this.getSkeleton().setSkin("biker1");
    }

    @Override
    protected void createPart(Body physicsBody, String bodyName, Sprite sprite, ArrayList<Joint> joints, ContainerEntityInterface containerEntity, Array<BleedablePoint> bleedablePoints)
    {
        if(bodyName.equals("bike"))
        {
            Bike part = new Bike(bodyName, sprite, physicsBody, joints, containerEntity);
            this.getDrawableEntities().put(bodyName, part);
            this.getInteractiveEntities().put(bodyName, part);
            this.getDetachableEntities().put(bodyName, part);
        }
        else
            super.createPart(physicsBody, bodyName, sprite, joints, containerEntity, bleedablePoints);
    }
}



